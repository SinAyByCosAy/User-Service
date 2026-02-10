package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.*;
import dev.tanay.userservice.models.*;
import dev.tanay.userservice.repositories.JwtKeyRepository;
import dev.tanay.userservice.repositories.SessionRepository;
import dev.tanay.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private JwtKeyRepository jwtKeyRepository;
    private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, JwtKeyRepository jwtKeyRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.jwtKeyRepository = jwtKeyRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public UserDto signup(SignupRequestDto signupRequestDto){
        User checkUser = userRepository.findUserByEmail(signupRequestDto.getEmail());
        //throw error if user already exists

        User newUser = new User();
        newUser.setEmail(signupRequestDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        userRepository.save(newUser);

        UserDto res = UserDto.from(newUser);
        return res;
    }
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequestDto){
        User checkUser = userRepository.findUserByEmail(loginRequestDto.getEmail());
        //throw error if user doesnt' exist
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), checkUser.getPassword()))
            System.out.println("Haye rama");//throw error

        //logging a user means, creating a new session
        Session session = new Session();
        JwtKeyEntity keyEntity = jwtKeyRepository.findByActiveTrue(); // only one active key
        MacAlgorithm alg = (MacAlgorithm) Jwts.SIG.get().get(keyEntity.getAlgorithm());
        SecretKey key = rebuildSecretKey(keyEntity);

        Instant now = Instant.now();
        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", checkUser.getEmail());
        jsonForJwt.put("roles", checkUser.getRoles());

// Create the compact JWS:
        String jwt = Jwts.builder()
                .claims(jsonForJwt)
                .subject(checkUser.getId().toString())
                .header()
                    .keyId(keyEntity.getKid())
                    .and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(5))))
                .signWith(key, alg)
                .compact();

        session.setToken(jwt);
        session.setUser(checkUser);
        session.setStatus(SessionStatus.ACTIVE);
        session.setExpiryAt(now.plus(Duration.ofHours(5)));
        sessionRepository.save(session);

        //Successful login -> add event to queue : for audit

        UserDto res = UserDto.from(checkUser);
        AuthResponseDto authResponse = new AuthResponseDto(res, jwt);
        return authResponse;
    }
    @Transactional
    public void logout(String token){
        //after this call user simply needs to be logged out, token should not be reused after this
        //it could be that token is valid, invalid, expired, stolen, whatever
        //we shouldn't throw errors here, simply success
        if(token == null) return;
        sessionRepository.updateStatus(token, SessionStatus.LOGGED_OUT);//if token is not found, nothing happens

        //Successful logout -> add event to queue : for audit
        //we shouldn't delete token as it will help in auditing later - not true, session will get invalidated in cache
        //we can run a background job to move very old tokens to a different DB - don't need this
    }
    @Transactional
    public SessionStatus validate(String token){
        if(token == null || token.isBlank()) return SessionStatus.INVALID;
        String[] parts = token.split("\\.");
        if(parts.length != 3) return SessionStatus.INVALID;

        Claims claims;
        try{ // token verification
            claims = Jwts.parser()
                    .keyLocator(header -> {
                        String kid = (String) header.get("kid"); // pick the correct secret that signed it
                        System.out.println(kid);
                        JwtKeyEntity keyEntity = jwtKeyRepository.findByKid(kid)
                                .orElseThrow(() -> new RuntimeException("Unknown key"));
                        if(keyEntity.getRetiredAt() != null && keyEntity.getRetiredAt().isBefore(Instant.now()))
                            throw new RuntimeException("Key retired");

                        return rebuildSecretKey(keyEntity);
                    })
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch(Exception e){ // verification failed
            System.out.println("Verification failed: " + e.getMessage());
            return SessionStatus.INVALID;
        }

        if(claims.getSubject() == null) { System.out.println("No id in token"); return SessionStatus.INVALID; }

        Optional<Session> checkSession = sessionRepository.findSessionByToken(token);
        if(checkSession.isEmpty()) { System.out.println("Session not found for the token"); return SessionStatus.INVALID; }

        // validating session
        Session session = checkSession.get();
        if(session.getStatus() != SessionStatus.ACTIVE)
            { System.out.println("Session Status is not active"); return session.getStatus(); }
        if(session.getExpiryAt().isBefore(Instant.now())){
            System.out.println("Session has expired");
            session.setStatus(SessionStatus.EXPIRED);
            return session.getStatus();
        }

        // no need to verify user details. A token is issued with user details.
        // if token is verified then user details are correct.
        // token won't be issued with wrong user details
        // and corrupted token won't get verified

        return SessionStatus.ACTIVE;
    }
    @Transactional
    public void insertSecret(){
        // Invalidate the old key?
        // do we invalidate all active sessions signed with the old key?
        // NO, that will be very poor design as a lot of users will be logged out.
        // We need OVERLAPPING KEYS: A token can tell which key signed it
        // Old Tokens -> verified with old key
        // New Tokens -> verified with new key
        // Then we retire the old key after TTL(max token lifetime + buffer)
        // no valid token will reference it and no session breaks

        // Get the time of the last token issued(which will have used the current active key)
        // There will be only one active key - current architecture
        Instant latestSessionExpiry = sessionRepository
                .findLatestSessionExpiry()
                .orElse(Instant.now());
        Instant retiredAt = latestSessionExpiry.plus(Duration.ofMinutes(5));

        // Set retiredAt value to old key and make set it as inactive
        // Active tokens can still get validated against the retiredAt time
        jwtKeyRepository.retireKey(retiredAt); // currently don't have a check for: if the key has already been retired(admin revocation), not needed right now

        //creating new secret
        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();
        String secretBase64 = Encoders.BASE64.encode(key.getEncoded());
        JwtKeyEntity keyEntity = new JwtKeyEntity();
        keyEntity.setAlgorithm("HS256");
        keyEntity.setSecretBase64(secretBase64);
        keyEntity.setActive(true);
        Instant now = Instant.now(); //UTC by default
        String kid = "key-" + DateTimeFormatter
                .ofPattern("yyyy-MM-dd-HH-mm")
                        .withZone(ZoneOffset.UTC)
                                .format(now);
        keyEntity.setKid(kid);
        keyEntity.setCreatedAt(now);
        jwtKeyRepository.save(keyEntity);
    }
    private SecretKey rebuildSecretKey(JwtKeyEntity keyEntity){
        byte[] keyBytes = Decoders.BASE64.decode(keyEntity.getSecretBase64());
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
}

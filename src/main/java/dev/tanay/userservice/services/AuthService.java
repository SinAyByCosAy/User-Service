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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private JwtKeyRepository jwtKeyRepository;
    private PasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, JwtKeyRepository jwtKeyRepository, PasswordEncoder passwordEncoder){
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
        JwtKeyEntity keyEntity = jwtKeyRepository.findTopByActiveTrueOrderByCreatedAtDesc();

        MacAlgorithm alg = (MacAlgorithm) Jwts.SIG.get().get(keyEntity.getAlgorithm());
        byte[] keyBytes = Decoders.BASE64.decode(keyEntity.getSecretBase64());
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Instant now = Instant.now();
        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", checkUser.getEmail());
        jsonForJwt.put("roles", checkUser.getRoles());

// Create the compact JWS:
        String jwt = Jwts.builder()
                .claims(jsonForJwt)
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
        //we shouldn't delete token as it will help in auditing later
        //we can run a background job to move very old tokens to a different DB
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
}

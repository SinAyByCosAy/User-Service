package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.*;
import dev.tanay.userservice.models.Role;
import dev.tanay.userservice.models.Session;
import dev.tanay.userservice.models.User;
import dev.tanay.userservice.repositories.SessionRepository;
import dev.tanay.userservice.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private PasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
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
        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

        //need user_id, email, roles
//        String message = "\"emailid\": \"tanay@gmail.com\",\n" +
//                "    \"Role\": [\n" +
//                "        \"student\",\n" +
//                "        \"mentor\"\n" +
//                "    ]";
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        Instant now = Instant.now();
        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", checkUser.getEmail());
        jsonForJwt.put("roles", checkUser.getRoles());

// Create the compact JWS:
        String jws = Jwts.builder()
                .claims(jsonForJwt)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(5))))
                .signWith(key, alg)
                .compact();

        session.setToken(jws);
        session.setUser(checkUser);
        session.setActive(true);
        session.setExpiryAt(now.plus(Duration.ofHours(5)));
        sessionRepository.save(session);

        UserDto res = UserDto.from(checkUser);
        AuthResponseDto authResponse = new AuthResponseDto(res, jws);
        return authResponse;
    }
    @Transactional
    public void logout(String token){
        //after this call user simply needs to be logged out, token should not be able to be reused after this
        //it could be that token is valid, invalid, expired, stolen, whatever
        //we shouldn't throw errors here, simply success
        if(token == null) return;
        sessionRepository.findSessionByTokenAndIsActiveTrueAndExpiryAtAfter(token, Instant.now()) //this call is only needed if we need session for blocklist, otherwise directly delete
                .ifPresent(session -> {
                   Long id = session.getUser().getId();
                   //add the token to blocklist here, use id to get expiry to set as TTL
                    System.out.println("Blocking token: " + token);
                   sessionRepository.deleteSessionByToken(token);
                });
    }
}

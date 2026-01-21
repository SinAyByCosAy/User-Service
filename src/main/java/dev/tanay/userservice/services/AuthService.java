package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.*;
import dev.tanay.userservice.models.Role;
import dev.tanay.userservice.models.Session;
import dev.tanay.userservice.models.SessionStatus;
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

        Instant now = Instant.now();
        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", checkUser.getEmail());
        jsonForJwt.put("roles", checkUser.getRoles());

// Create the compact JWS:
        String jwt = Jwts.builder()
                .claims(jsonForJwt)
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
        sessionRepository.updateStatus(token, SessionStatus.LOGGED_OUT);
        //we shouldn't delete token as it will help in auditing later
        //we can run a background job to move very old tokens to a different DB
    }
}

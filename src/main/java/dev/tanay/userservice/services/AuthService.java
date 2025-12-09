package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.AuthResponseDto;
import dev.tanay.userservice.dtos.LoginRequestDto;
import dev.tanay.userservice.dtos.SignupRequestDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.models.Role;
import dev.tanay.userservice.models.Session;
import dev.tanay.userservice.models.User;
import dev.tanay.userservice.repositories.SessionRepository;
import dev.tanay.userservice.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

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
    public UserDto signup(SignupRequestDto signupRequestDto){
        User checkUser = userRepository.findUserByEmail(signupRequestDto.getEmail());
        //throw error if user already exists

        User newUser = new User();
        newUser.setEmail(signupRequestDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        userRepository.save(newUser);

        UserDto res = new UserDto();
        res.setEmail(newUser.getEmail());
        return res;
    }
    public AuthResponseDto login(LoginRequestDto loginRequestDto){
        User checkUser = userRepository.findUserByEmail(loginRequestDto.getEmail());
        //throw error if user doesnt' exist
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), checkUser.getPassword()))
            System.out.println("Haye rama");//throw error
        //logging a user means, creating a new session
        String token = RandomStringUtils.randomAlphanumeric(30);
        Session session = new Session();
        session.setToken(token);
        session.setUser(checkUser);
        session.setActive(true);
        sessionRepository.save(session);

        UserDto res = new UserDto();
        res.setEmail(checkUser.getEmail());
        Set<String> roles = new HashSet<>();
        for(Role role : checkUser.getRoles()) roles.add(role.getName());
        res.setRoles(roles);

        AuthResponseDto authResponse = new AuthResponseDto(res, token);
        return authResponse;
    }
    public void logout(){}
}

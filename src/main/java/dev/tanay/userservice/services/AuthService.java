package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.SignupRequestDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.models.User;
import dev.tanay.userservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
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
    public void login(){}
    public void logout(){}
}

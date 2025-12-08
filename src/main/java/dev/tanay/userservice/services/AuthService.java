package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.LoginRequestDto;
import dev.tanay.userservice.dtos.SignupRequestDto;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public void signup(SignupRequestDto signupRequestDto){
        System.out.println(signupRequestDto.getEmail() + " " + signupRequestDto.getPassword());
    }
    public void login(LoginRequestDto loginRequestDto){
        System.out.println(loginRequestDto.getEmail() + " " + loginRequestDto.getPassword());
    }
}

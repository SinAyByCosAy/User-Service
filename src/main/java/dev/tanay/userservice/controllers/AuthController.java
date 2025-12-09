package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.SignupRequestDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupRequestDto signupRequestDto){
        return authService.signup(signupRequestDto);
    }
    @PostMapping("/login")
    public void login(){

    }
    @PostMapping("/logout")
    public void logout(){

    }
}

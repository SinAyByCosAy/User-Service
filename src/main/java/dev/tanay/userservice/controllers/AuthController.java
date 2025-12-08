package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.LoginRequestDto;
import dev.tanay.userservice.dtos.LogoutRequestDto;
import dev.tanay.userservice.dtos.SignupRequestDto;
import dev.tanay.userservice.services.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequestDto signupRequestDto){
        authService.signup(signupRequestDto);
    }
    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto loginRequestDto){
        authService.login(loginRequestDto);
    }
    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequestDto logoutRequestDto){
        authService.logout(logoutRequestDto);
    }
}

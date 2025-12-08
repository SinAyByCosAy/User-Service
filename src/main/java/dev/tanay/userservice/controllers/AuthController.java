package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.LoginRequestDto;
import dev.tanay.userservice.dtos.LogoutRequestDto;
import dev.tanay.userservice.dtos.SignupRequestDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequestDto signupRequestDto){
        System.out.println(signupRequestDto);
    }
    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto loginRequestDto){
        System.out.println(loginRequestDto.getEmail() + " " + loginRequestDto.getPassword());
    }
    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequestDto logoutRequestDto){
        System.out.println(logoutRequestDto.getUserId());
    }
}

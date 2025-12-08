package dev.tanay.userservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/signup")
    public void signup(SignupRequestDto signupRequestDto){
        System.out.println(signupRequestDto)
    }
}

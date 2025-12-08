package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.SignupRequestDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/signup")
    public void signup(SignupRequestDto signupRequestDto){
        System.out.println(signupRequestDto);
    }
}

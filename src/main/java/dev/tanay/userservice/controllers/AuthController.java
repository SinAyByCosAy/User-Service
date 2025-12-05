package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.*;
import dev.tanay.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserDto> createUser(@RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(
                authService.createUser(signUpRequestDto),
                HttpStatus.CREATED
        );
    }
    @PostMapping("/login")
    public ResponseEntity<LoggedInUserDto> loginUser(@RequestBody LoginRequestDto loginRequestDto){
        return new ResponseEntity<>(
                authService.loginUser(loginRequestDto),
                HttpStatus.ACCEPTED
        );
    }
    @PostMapping("/logout")
    public HttpStatus logoutUser(@RequestBody LogoutRequestDto logoutRequestDto){
        authService.logoutUser(logoutRequestDto);
        return HttpStatus.OK;
    }
}

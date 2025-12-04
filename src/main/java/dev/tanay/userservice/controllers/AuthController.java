package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.LoggedInUserDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.dtos.UserRequestDto;
import dev.tanay.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserRequestDto userReq){
        return new ResponseEntity<>(
                authService.createUser(userReq),
                HttpStatus.CREATED
        );
    }
    @PostMapping("/login")
    public ResponseEntity<LoggedInUserDto> loginUser(@RequestBody UserRequestDto userReq){
        return new ResponseEntity<>(
                authService.loginUser(userReq),
                HttpStatus.ACCEPTED
        );
    }
    @PostMapping("/logout")
    public HttpStatus logoutUser(@RequestBody LoggedInUserDto loggedInUserDto){
        authService.logoutUser(loggedInUserDto);
        return HttpStatus.OK;
    }
}

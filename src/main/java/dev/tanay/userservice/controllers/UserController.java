package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.LoggedInUserDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.dtos.UserRequestDto;
import dev.tanay.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserRequestDto userReq){
        return new ResponseEntity<>(
                userService.createUser(userReq),
                HttpStatus.CREATED
        );
    }
    @PostMapping("/login")
    public ResponseEntity<LoggedInUserDto> loginUser(@RequestBody UserRequestDto userReq){
        return new ResponseEntity<>(
                userService.loginUser(userReq),
                HttpStatus.ACCEPTED
        );
    }
    @PostMapping("/logout")
    public HttpStatus logoutUser(@RequestBody LoggedInUserDto loggedInUserDto){
        userService.logoutUser(loggedInUserDto);
        return HttpStatus.OK;
    }
}

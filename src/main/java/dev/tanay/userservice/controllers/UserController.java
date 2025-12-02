package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.LoggedInUserDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.models.User;
import dev.tanay.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<UserDto> createUser(@RequestBody User user){
        return new ResponseEntity<>(
                userService.createUser(user),
                HttpStatus.CREATED
        );
    }
    @PostMapping("/login")
    public ResponseEntity<LoggedInUserDto> loginUser(@RequestBody User user){
        return new ResponseEntity<>(
                userService.loginUser(user),
                HttpStatus.ACCEPTED
        );
    }
    @PostMapping("/logout/{id}")
    public HttpStatus logoutUser(@PathVariable Long id){
        userService.logoutUser(id);
        return HttpStatus.OK;
    }
}

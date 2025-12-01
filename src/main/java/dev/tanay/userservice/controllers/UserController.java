package dev.tanay.userservice.controllers;

import dev.tanay.userservice.models.User;
import dev.tanay.userservice.services.UserService;
import org.springframework.http.HttpStatus;
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
    public HttpStatus createUser(@RequestBody User user){
        userService.createUser(user);
        return HttpStatus.CREATED;
    }
}

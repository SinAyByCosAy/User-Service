package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.AuthResponseDto;
import dev.tanay.userservice.dtos.LoginRequestDto;
import dev.tanay.userservice.dtos.SignupRequestDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.services.AuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

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
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto){
        AuthResponseDto authResponse = authService.login(loginRequestDto);
        ResponseCookie cookie = ResponseCookie
                .from("auth_token",authResponse.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(5))
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse.getUserDto());
    }
    @PostMapping("/logout")
    public void logout(){

    }
}

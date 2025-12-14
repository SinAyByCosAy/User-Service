package dev.tanay.userservice.controllers;

import dev.tanay.userservice.dtos.*;
import dev.tanay.userservice.services.AuthService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> logout(@CookieValue(name = "auth_token", required = false) String token){
        //we shouldn't require body from user for logout as token contains everything(JWT).
        //validate token and add it to blocklist with remaining expiry as the TTL
        //and reset cookie
        //logout should simply logout if token is there, else exit. It shouldn't throw errors.
        authService.logout(token);
        ResponseCookie cookie = ResponseCookie
                .from("auth_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(0))
                .sameSite("Lax")
                .build();
        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}

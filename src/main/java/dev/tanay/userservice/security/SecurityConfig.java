package dev.tanay.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for non-browser clients (Postman/React)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow EVERYTHING
                );

        return http.build();
    }
}

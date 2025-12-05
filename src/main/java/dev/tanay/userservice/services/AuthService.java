package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.*;

public interface AuthService {
    public UserDto createUser(SignUpRequestDto signUpRequestDto);
    public LoggedInUserDto loginUser(LoginRequestDto loginRequestDto);
    public void logoutUser(LogoutRequestDto logoutRequestDto);
}

package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.LoggedInUserDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.dtos.UserRequestDto;

public interface AuthService {
    public UserDto createUser(UserRequestDto userReq);
    public LoggedInUserDto loginUser(UserRequestDto userReq);
    public void logoutUser(LoggedInUserDto loggedInUserDto);
}

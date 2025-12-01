package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.models.User;

public interface UserService {
    public UserDto createUser(User user);
    public void loginUser(User user);
    public void logoutUser(Long id);
}

package dev.tanay.userservice.services;

import dev.tanay.userservice.models.User;

public interface UserService {
    public void createUser(User user);
    public void loginUser(User user);
    public void logoutUser(Long id);
}

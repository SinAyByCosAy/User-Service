package dev.tanay.userservice.services;

import dev.tanay.userservice.models.User;
//import dev.tanay.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceDB implements UserService{
//    private UserRepository userRepository;
//    public UserServiceDB(UserRepository userRepository){
//        this.userRepository = userRepository;
//    }
    public void createUser(User user){
//        userRepository.createUser(user);
    }
    public void loginUser(User user){
//        userR
    }
    public void logoutUser(Long id){

    }
}

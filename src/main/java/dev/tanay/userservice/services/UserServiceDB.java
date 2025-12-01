package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.exceptions.NotFoundException;
import dev.tanay.userservice.models.User;
import dev.tanay.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceDB implements UserService{
    private UserRepository userRepository;
    public UserServiceDB(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDto createUser(User user){
        userRepository.save(user);
        UserDto createdUser = new UserDto();
        setUserDto(createdUser, user);
        return createdUser;
    }
    @Override
    public void loginUser(User user){
        User fetchUser = userRepository.findUserByEmail(user.getEmail());
        if(!fetchUser.getPassword().equals(user.getPassword())){
            throw new NotFoundException("Invalid Password");
        }
    }
    @Override
    public void logoutUser(Long id){
//        userRepository.deleteUser(id);
    }
    private void setUserDto(UserDto createdUser, User user){
        createdUser.setId(user.getId());
        createdUser.setEmail(user.getEmail());
    }
}

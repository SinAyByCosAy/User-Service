package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.LoggedInUserDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.exceptions.NotFoundException;
import dev.tanay.userservice.models.Session;
import dev.tanay.userservice.models.User;
import dev.tanay.userservice.repositories.SessionRepository;
import dev.tanay.userservice.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserServiceDB implements UserService{
    private UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public UserServiceDB(UserRepository userRepository,
                         SessionRepository sessionRepository){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    @Override
    @Transactional
    public UserDto createUser(User user){
        userRepository.save(user);
        UserDto createdUser = new UserDto();
        setUserDto(createdUser, user);
        return createdUser;
    }
    @Override
    @Transactional
    public LoggedInUserDto loginUser(User user){
        User fetchUser = userRepository.findUserByEmail(user.getEmail());
        if(!fetchUser.getPassword().equals(user.getPassword())){
            throw new NotFoundException("Invalid Password");
        }
        Session newSession = new Session();
        newSession.setToken("kl1234pr" + getRandomNumberUsingNextInt(1, 1000));
        newSession.setUser(fetchUser);
        sessionRepository.save(newSession);
        return getLoggedInUserDto(fetchUser, newSession);
    }
    @Override
    public void logoutUser(Long id){
//        userRepository.deleteUser(id);
    }
    private void setUserDto(UserDto createdUser, User user){
        createdUser.setId(user.getId());
        createdUser.setEmail(user.getEmail());
    }
    private LoggedInUserDto getLoggedInUserDto(User fetchUser, Session newSession){
        LoggedInUserDto loggedInUserDto = new LoggedInUserDto();
        loggedInUserDto.setId(fetchUser.getId());
        loggedInUserDto.setEmail(fetchUser.getEmail());
        loggedInUserDto.setToken(newSession.getToken());
        return loggedInUserDto;
    }
    private int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}

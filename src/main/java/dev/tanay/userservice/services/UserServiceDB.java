package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.LoggedInUserDto;
import dev.tanay.userservice.dtos.UserDto;
import dev.tanay.userservice.dtos.UserRequestDto;
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
    public UserDto createUser(UserRequestDto userReq){
        User newUser = new User();
        newUser.setEmail(userReq.getEmail());
        newUser.setPassword(userReq.getPassword());
        userRepository.save(newUser);

        UserDto userResponse = new UserDto();
        setUserDto(userResponse, newUser);
        return userResponse;
    }
    @Override
    @Transactional
    public LoggedInUserDto loginUser(UserRequestDto userReq){
        User fetchUser = userRepository.findUserByEmail(userReq.getEmail());
        if(!fetchUser.getPassword().equals(userReq.getPassword())){
            throw new NotFoundException("Invalid Password");
        }
        Session newSession = new Session();
        newSession.setToken("kl1234pr" + getRandomNumberUsingNextInt(1, 1000));
        newSession.setUser(fetchUser);
        sessionRepository.save(newSession);
        return getLoggedInUserDto(fetchUser, newSession);
    }
    @Override
    @Transactional
    public void logoutUser(LoggedInUserDto loggedInUserDto){
        Session sessionActive = sessionRepository.findSessionByToken(loggedInUserDto.getToken());
        if(sessionActive == null) throw new NotFoundException("Invalid Token, how are you even logged in?");
        sessionRepository.deleteByToken(loggedInUserDto.getToken());
    }
    private void setUserDto(UserDto userResponse, User newUser){
        userResponse.setId(newUser.getId());
        userResponse.setEmail(newUser.getEmail());
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

package dev.tanay.userservice.services;

import dev.tanay.userservice.dtos.*;
import dev.tanay.userservice.exceptions.NotFoundException;
import dev.tanay.userservice.exceptions.ResourceAlreadyExistsException;
import dev.tanay.userservice.models.Session;
import dev.tanay.userservice.models.User;
import dev.tanay.userservice.repositories.SessionRepository;
import dev.tanay.userservice.repositories.AuthRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthServiceDB implements AuthService {
    private AuthRepository authRepository;
    private final SessionRepository sessionRepository;

    public AuthServiceDB(AuthRepository authRepository,
                         SessionRepository sessionRepository){
        this.authRepository = authRepository;
        this.sessionRepository = sessionRepository;
    }
    @Override
    @Transactional
    public UserDto createUser(SignUpRequestDto signUpRequestDto){
        User checkUser = authRepository.findUserByEmail(signUpRequestDto.getEmail());
        if(checkUser != null) throw new ResourceAlreadyExistsException(signUpRequestDto.getEmail() + " already exists, try login.");
        User newUser = new User();
        newUser.setEmail(signUpRequestDto.getEmail());
        //need encryption here
        newUser.setPassword(signUpRequestDto.getPassword());
        authRepository.save(newUser);

        UserDto userResponse = new UserDto();
        setUserDto(userResponse, newUser);
        return userResponse;
    }
    @Override
    @Transactional
    public LoggedInUserDto loginUser(LoginRequestDto loginRequestDto){
        User fetchUser = authRepository.findUserByEmail(loginRequestDto.getEmail());
        if(fetchUser == null) throw new NotFoundException("User doesn't exist.");
        //need to check encrypted password
        if(!fetchUser.getPassword().equals(loginRequestDto.getPassword())) throw new NotFoundException("Invalid Password");
        Session newSession = new Session();
        newSession.setToken(RandomStringUtils.randomAlphanumeric(30));
        newSession.setUser(fetchUser);
        sessionRepository.save(newSession);
        return getLoggedInUserDto(fetchUser, newSession);
    }
    @Override
    @Transactional
    public void logoutUser(LogoutRequestDto logoutRequestDto){
        Session sessionActive = sessionRepository.findSessionByToken(logoutRequestDto.getToken());
        if(sessionActive == null) throw new NotFoundException("Invalid Token, how are you even logged in?");
        sessionRepository.deleteByToken(logoutRequestDto.getToken());
    }
    private void setUserDto(UserDto userResponse, User newUser){
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

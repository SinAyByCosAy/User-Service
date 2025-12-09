package dev.tanay.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {
    private UserDto userDto;
    private String token;
    public AuthResponseDto(UserDto userDto, String token){
        this.userDto = userDto;
        this.token = token;
    }
}

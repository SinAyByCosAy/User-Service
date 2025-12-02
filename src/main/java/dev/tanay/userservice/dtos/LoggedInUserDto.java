package dev.tanay.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggedInUserDto {
    private Long id;
    private String email;
    private String token;
}

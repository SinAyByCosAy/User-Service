package dev.tanay.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessageDto {
    private Long id;
    private String username;
    private String email;
}

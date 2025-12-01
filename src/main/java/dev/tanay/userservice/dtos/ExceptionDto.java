package dev.tanay.userservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ExceptionDto {
    private String message;
    private HttpStatus errorCode;
    public ExceptionDto(String message, HttpStatus errorCode){
        this.message = message;
        this.errorCode = errorCode;
    }
}

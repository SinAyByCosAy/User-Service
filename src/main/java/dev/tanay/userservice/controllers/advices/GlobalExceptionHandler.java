package dev.tanay.userservice.controllers.advices;

import dev.tanay.userservice.dtos.ExceptionDto;
import dev.tanay.userservice.exceptions.NotFoundException;
import dev.tanay.userservice.exceptions.ResourceAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException notFoundException){
        return new ResponseEntity<>(
                new ExceptionDto(notFoundException.getMessage(), HttpStatus.UNAUTHORIZED),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException resourceAlreadyExistsException){
        return new ResponseEntity<>(
                new ExceptionDto(resourceAlreadyExistsException.getMessage(), HttpStatus.CONFLICT),
                HttpStatus.CONFLICT
        );
    }
}

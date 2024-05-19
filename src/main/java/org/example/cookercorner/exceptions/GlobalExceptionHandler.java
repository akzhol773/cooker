package org.example.cookercorner.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.dtos.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(RecipeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleRecipeNotFoundException(
            RecipeNotFoundException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadException(FileUploadException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<?> handleEmailAlreadyExistException(EmailAlreadyExistException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserRoleNotFoundException.class)
    public ResponseEntity<Object> handleUserRoleNotFoundException(
            UserRoleNotFoundException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<?> handlePasswordNotMatchException(PasswordNotMatchException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(
            BadCredentialsException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleNotAuthorizedException(
            NotAuthorizedException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidJsonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleInvalidJsonException(
            InvalidJsonException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleUserNotFoundException(
            UserNotFoundException ex) {
        UUID exceptionUUID = UUID.randomUUID();
        log.error("Exception UUID: {}", exceptionUUID, ex);
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now().toString(), ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }









}
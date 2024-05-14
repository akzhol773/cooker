package org.example.cookercorner.services;

import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    String createNewUser(UserRequestDto registrationUserDto);
    JwtResponseDto authenticate(JwtRequestDto authRequest);


}

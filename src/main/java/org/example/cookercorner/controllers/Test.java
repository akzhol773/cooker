package org.example.cookercorner.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class Test {

    private  final JwtTokenUtils jwtTokenUtils;
    private  final  UserRepository userRepository;

    @Autowired
    public  Test(JwtTokenUtils jwtTokenUtils, UserRepository userRepository){
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
    }
    @Operation(
            summary = "Testing endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns string"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/test")
    public String test(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        Optional<User> user = userRepository.findById(userId);
        return "Hello " + user.get().getName();
    }
}

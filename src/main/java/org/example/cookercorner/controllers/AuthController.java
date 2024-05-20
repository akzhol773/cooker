package org.example.cookercorner.controllers;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.example.cookercorner.dtos.*;
import org.example.cookercorner.services.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("api/auth/")
public class AuthController {

    AuthService authService;

    @Operation(
            summary = "Login",
            description = "Endpoint for getting tokens after login"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned a token"),
            @ApiResponse(responseCode = "403", description = "Username or password is invalid"),
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody JwtRequestDto authRequest){
       return  ResponseEntity.ok().body(authService.authenticate(authRequest));

    }

    @Operation(
            summary = "Registration",
            description = "Endpoint for customer to register a new account. Requires a body"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "The provided username is already taken"),
            @ApiResponse(responseCode = "409", description = "The provided email is already taken")
    })

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequestDto registrationUserDto){
        return  ResponseEntity.ok().body(authService.createNewUser(registrationUserDto));
    }

    @Operation(
            summary = "Refresh the token",
            description = "If the token is expired then it is possible to generate a new access token using refresh token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned a new access token"),
            @ApiResponse(responseCode = "403", description = "Token has expired"),
            @ApiResponse(responseCode = "403", description = "Token not found"),

    })

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtRefreshTokenDto> refreshToken(@RequestParam("refreshToken") String refreshToken){
        return  ResponseEntity.ok(authService.refreshToken(refreshToken));

    }



}

package org.example.cookercorner.services.Impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.exceptions.EmailAlreadyExistException;
import org.example.cookercorner.exceptions.InvalidTokenException;
import org.example.cookercorner.exceptions.PasswordNotMatchException;
import org.example.cookercorner.exceptions.UserNotFoundException;
import org.example.cookercorner.mapper.UserMapper;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    JwtTokenUtils jwtTokenUtils;
    UserMapper userMapper;

    @Autowired
    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public String createNewUser(UserRequestDto registrationUserDto) {
        if (userRepository.findByEmail(registrationUserDto.email()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exist. Please, try to use another one.");
        }
        if (!registrationUserDto.password().equals(registrationUserDto.confirmPassword())) {
            throw new PasswordNotMatchException("Passwords do not match.");
        }
        userRepository.save(userMapper.toEntity(registrationUserDto, passwordEncoder));
        return "User created successfully!";
    }

    @Override
    @Transactional
    public JwtResponseDto authenticate(JwtRequestDto authRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtTokenUtils.generateAccessToken(user);
            String refreshToken = jwtTokenUtils.generateRefreshToken(user);

            return new JwtResponseDto(user.getEmail(), accessToken, refreshToken);
        } catch (AuthenticationException exception) {
            if (exception instanceof BadCredentialsException) {
                throw new BadCredentialsException("Invalid email or password");
            } else {
                throw new DisabledException("User is not enabled yet");
            }
        }
    }


    @Override
    public JwtRefreshTokenDto refreshToken(String refreshToken) {
        try {
            if (refreshToken == null) {
                throw new InvalidTokenException("Token can not be null!");
            }

            String usernameFromRefreshToken = jwtTokenUtils.getEmailFromRefreshToken(refreshToken);
            if (usernameFromRefreshToken == null) {
                throw new UsernameNotFoundException("Username not found!");
            }
            User user = userRepository.findByEmail(usernameFromRefreshToken).orElseThrow(() ->
                    new UserNotFoundException("User not found"));
            String accessToken = jwtTokenUtils.generateAccessToken(user);
            return new JwtRefreshTokenDto(usernameFromRefreshToken, accessToken);

        } catch (Exception e) {
            throw new InvalidTokenException("Token is invalid!");
        }
    }
}

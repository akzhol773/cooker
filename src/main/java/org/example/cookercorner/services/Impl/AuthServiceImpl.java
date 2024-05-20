package org.example.cookercorner.services.Impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
import org.example.cookercorner.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public JwtResponseDto authenticate(JwtRequestDto authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
            );
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtTokenUtils.generateAccessToken(user);
            String refreshToken = jwtTokenUtils.generateRefreshToken(user);
            return new JwtResponseDto(authRequest.email(), accessToken, refreshToken);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password", ex);
        } catch (AuthenticationException ex) {
            throw new AuthenticationServiceException("Authentication failed for user: " + authRequest.email(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("An unexpected error occurred during authentication", ex);
        }
    }


    @Override
    public JwtRefreshTokenDto refreshToken(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new BadCredentialsException("Credentials cannot be empty!");
            }
            if (jwtTokenUtils.isTokenExpired(refreshToken)) {
                throw new InvalidTokenException("The token is expired!");
            }
            String userEmailFromRefreshToken = jwtTokenUtils.getEmailFromRefreshToken(refreshToken);
            if (userEmailFromRefreshToken == null) {
                throw new InvalidTokenException("The token is invalid!");
            }
            User user = userRepository.findByEmail(userEmailFromRefreshToken)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            String newAccessToken = jwtTokenUtils.generateAccessToken(user);

            return new JwtRefreshTokenDto(userEmailFromRefreshToken, newAccessToken);
        } catch (BadCredentialsException | UserNotFoundException | InvalidTokenException ex) {
            throw new InvalidTokenException("An unexpected error occurred while refreshing the token!");
        }
    }
}

package org.example.cookercorner.services.Impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.exceptions.EmailAlreadyExistException;
import org.example.cookercorner.exceptions.PasswordNotMatchException;
import org.example.cookercorner.mapper.UserMapper;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.AuthService;
import org.example.cookercorner.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserService userService;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    JwtTokenUtils jwtTokenUtils;
    UserMapper userMapper;

    @Override
    @Transactional
    public String createNewUser(UserRequestDto registrationUserDto) {

        if (userService.findUserByEmail(registrationUserDto.email()).isPresent()) {
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
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
            User user = (User) authentication.getPrincipal();
            return new JwtResponseDto(authRequest.email(),
                    jwtTokenUtils.generateAccessToken(user));
        } catch (AuthenticationException exception) {
                throw new BadCredentialsException("Invalid username or password");
        }
    }
}

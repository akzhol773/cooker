package org.example.cookercorner.mapper;

import org.example.cookercorner.dtos.UserRequestDto;
import org.example.cookercorner.entities.Role;
import org.example.cookercorner.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto dto, Role userRole, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setRoles(Collections.singletonList(userRole));
        user.setPassword(passwordEncoder.encode(dto.password()));
        return user;
    }
}

package org.example.cookercorner.mapper;

import org.example.cookercorner.dtos.UserRequestDto;
import org.example.cookercorner.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto dto, String userRole, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(dto.password()));
        return user;
    }
}

package org.example.cookercorner.dtos;

import lombok.Builder;


@Builder
public record UserRequestDto(
        String email,
        String name,
        String password,
        String confirmPassword) {}
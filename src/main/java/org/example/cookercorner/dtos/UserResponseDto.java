package org.example.cookercorner.dtos;

import lombok.Builder;


@Builder
public record UserResponseDto(String username, String status) {
}
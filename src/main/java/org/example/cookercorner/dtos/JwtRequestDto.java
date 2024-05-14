package org.example.cookercorner.dtos;


import lombok.Builder;

@Builder
public record JwtRequestDto(String email, String password) {
}
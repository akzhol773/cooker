package org.example.cookercorner.dtos;


import lombok.Builder;

@Builder
public record JwtResponseDto(String username, String accessToken){
}
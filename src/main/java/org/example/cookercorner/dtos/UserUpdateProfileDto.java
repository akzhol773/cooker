package org.example.cookercorner.dtos;


import lombok.Builder;

@Builder
public record UserUpdateProfileDto(String name, String biography) {
}
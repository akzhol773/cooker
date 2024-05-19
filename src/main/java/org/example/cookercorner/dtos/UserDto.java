package org.example.cookercorner.dtos;

import java.io.Serializable;


public record UserDto(Long id, String name, String photoUrl) {
}
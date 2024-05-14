package org.example.cookercorner.dtos;

import lombok.Builder;

@Builder
public record ExceptionDto(
        String time,
        String message
) {
}

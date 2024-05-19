package org.example.cookercorner.dtos;

import lombok.Builder;

@Builder
public record RecipeSearchDto(Long id, String image, String recipeName) {
}

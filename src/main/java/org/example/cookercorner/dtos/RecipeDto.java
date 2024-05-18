package org.example.cookercorner.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.example.cookercorner.entities.Recipe}
 */
@Builder
public record RecipeDto(
        Long id,
        String recipeName,
        String imageUrl,
        String author,
        String cookingTime,
        String difficulty,
        int likeQuantity,
        boolean isLiked,
        boolean isSaved,
        String description,
        List<IngredientDto> ingredients


) implements Serializable {
}
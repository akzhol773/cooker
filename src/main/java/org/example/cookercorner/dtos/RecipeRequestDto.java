package org.example.cookercorner.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.example.cookercorner.entities.Recipe}
 */
public record RecipeRequestDto(

                        String recipeName,

                         String description,

                         String category,

                         String difficulty,

                         String cookingTime,
                         List<IngredientRequestDto> ingredients
                         ) implements Serializable {
}
package org.example.cookercorner.dtos;

import java.io.Serializable;

/**
 * DTO for {@link org.example.cookercorner.entities.Ingredient}
 */
public record IngredientRequestDto(String name, String amount, String unitOfMeasurement) implements Serializable {
}
package org.example.cookercorner.dtos;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RecipeListDto(Long id, String image, String recipeName, String author, Integer likesQuantity, Integer savesQuantity, boolean isSaved, boolean isLiked) implements Serializable {

}
package org.example.cookercorner.dtos;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RecipeListDto(Long id, String image, String recipeName, String author, int likesQuantity, int savesQuantity, boolean isSaved, boolean isLiked) {

}
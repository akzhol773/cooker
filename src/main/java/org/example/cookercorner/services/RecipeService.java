package org.example.cookercorner.services;

import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface RecipeService {
    List<RecipeListDto> getByCategory(Category categoryEnum, Long userId);

    List<RecipeListDto> getMyRecipe(Long userId);

    List<RecipeListDto> getMySavedRecipe(Long userId);


    RecipeDto getRecipeById(Long recipeId, Long userId);

    void addRecipe(RecipeRequestDto requestDto, MultipartFile image, Long userId);

    List<RecipeListDto> searchRecipes(String query, Long userId);

    boolean isLiked(Long recipeId, Long currentUserId);

    Optional<Recipe> findRecipeById(Long recipeId);

    void removeLikeFromRecipe(Long recipeId, Long currentUserId);

    void putLikeIntoRecipe(Long recipeId, Long currentUserId);

    boolean isSaved(Long recipeId, Long currentUserId);

    void removeSaveFromRecipe(Long recipeId, Long currentUserId);

    void putSaveIntoRecipe(Long recipeId, Long currentUserId);

    int getUserRecipeQuantity(User user);
}

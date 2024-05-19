package org.example.cookercorner.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface RecipeService {
    

    List<RecipeListDto> getMyRecipe(Authentication authentication);

    List<RecipeListDto> getMySavedRecipe(Authentication authentication);


    RecipeDto getRecipeById(Long recipeId, Authentication authentication);

  

    List<RecipeListDto> searchRecipes(String query, Authentication authentication);

    boolean isLiked(Long recipeId, Long currentUserId);


    void removeLikeFromRecipe(Long recipeId, Long currentUserId);

    void putLikeIntoRecipe(Long recipeId, Long currentUserId);

    boolean isSaved(Long recipeId, Long currentUserId);

    void removeSaveFromRecipe(Long recipeId, Long currentUserId);

    void putSaveIntoRecipe(Long recipeId, Long currentUserId);

    int getUserRecipeQuantity(User user);

    List<RecipeListDto> getByCategory(Authentication authentication, String category);

    String addRecipe(String recipeDto, MultipartFile image, Authentication authentication) throws FileUploadException;
}

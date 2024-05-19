package org.example.cookercorner.mapper;


import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.Ingredient;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.enums.Difficulty;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.repositories.RecipeRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {
    private final RecipeRepository recipeRepository;
    public RecipeMapper(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }
    public RecipeListDto toRecipeListDto(Recipe recipe, boolean isLikedByUser, boolean isSavedByUser) {
        return new RecipeListDto(
                recipe.getId(),
                getImageUrl(recipe),
                recipe.getRecipeName(),
                getCreatorName(recipe),
                getLikesCount(recipe),
                getSavesCount(recipe),
                isSavedByUser,
                isLikedByUser
        );
    }
    public List<RecipeListDto> toRecipeListDtoList(List<Recipe> recipes, Long userId) {
        return recipes.stream()
                .map(recipe -> toRecipeListDto(recipe,
                        isLiked(recipe.getId(), userId),
                        isSaved(recipe.getId(), userId)))
                .collect(Collectors.toList());
    }
    public RecipeDto toRecipeDto(Recipe recipe, Long userId) {
        return new RecipeDto(
                recipe.getId(),
                recipe.getRecipeName(),
                getImageUrl(recipe),
                getCreatorName(recipe),
                recipe.getCookingTime(),
                getDifficulty(recipe),
                getLikesCount(recipe),
                isLiked(recipe.getId(), userId),
                isSaved(recipe.getId(), userId),
                recipe.getDescription(),
                mapIngredients(recipe.getIngredients())
        );
    }
    private String getImageUrl(Recipe recipe) {
        return (recipe.getPhoto() != null) ? recipe.getImage().getUrl() : null;
    }

    private String getCreatorName(Recipe recipe) {
        return (recipe.getCreatedBy() != null) ? recipe.getCreatedBy().getName() : "Unknown";
    }
    private int getLikesCount(Recipe recipe) {
        return recipe.getLikes().size();
    }

    private int getSavesCount(Recipe recipe) {
        return recipe.getSaves().size();
    }

    private String getDifficulty(Recipe recipe) {
        return (recipe.getDifficulty() != null) ? recipe.getDifficulty().name() : "Unknown";
    }

    private List<IngredientDto> mapIngredients(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> new IngredientDto(ingredient.getId(), ingredient.getName(), ingredient.getAmount()))
                .collect(Collectors.toList());
    }

    private boolean isLiked(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getLikes().stream().anyMatch(user -> user.getId().equals(userId));
    }


    private boolean isSaved(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getSaves().stream().anyMatch(user -> user.getId().equals(userId));
    }


    public Recipe toEntity(RecipeRequestDto requestDto, String photo, User user) {
        String category = requestDto.category().toUpperCase();
        String difficulty = requestDto.difficulty().toUpperCase();
        Recipe recipe = new Recipe();
        recipe.setRecipeName(requestDto.recipeName());
        recipe.setCategory(Category.valueOf(category));
        recipe.setDifficulty(Difficulty.valueOf(difficulty));
        recipe.setDescription(requestDto.description());
        recipe.setPhoto(photo);
        recipe.setCookingTime(requestDto.cookingTime());
        recipe.setCreatedBy(user);
        List<Ingredient> ingredients = requestDto.ingredients().stream()
                .map(ingredientDto -> {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setRecipe(recipe);
                    ingredient.setName(ingredientDto.name());
                    ingredient.setUnitOfMeasurement(ingredientDto.unitOfMeasurement());
                    ingredient.setAmount(ingredientDto.amount());
                    return ingredient;
                })
                .collect(Collectors.toList());
        recipe.setIngredients(ingredients);
        return recipe;
    }
}
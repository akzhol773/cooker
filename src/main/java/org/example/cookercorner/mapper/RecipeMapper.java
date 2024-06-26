package org.example.cookercorner.mapper;


import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.Ingredient;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.enums.Difficulty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    public RecipeListDto toRecipeListDto(Recipe recipe, boolean isLikedByUser, boolean isSavedByUser) {
        return new RecipeListDto(
                recipe.getId(),
                recipe.getPhotoUrl(),
                recipe.getRecipeName(),
                recipe.getCreatedBy().getName(),
                recipe.getLikes().size(),
                recipe.getSaves().size(),
                isSavedByUser,
                isLikedByUser
        );
    }

    public RecipeDto toRecipeDto(Recipe recipe, boolean isLiked, boolean isSaved) {
        return new RecipeDto(
                recipe.getId(),
                recipe.getRecipeName(),
                recipe.getPhotoUrl(),
                recipe.getCreatedBy().getName(),
                recipe.getCookingTime(),
                recipe.getDifficulty().toString(),
                recipe.getLikes().size(),
                isLiked,
                isSaved,
                recipe.getDescription(),
                mapIngredients(recipe.getIngredients())
        );
    }


    private List<IngredientDto> mapIngredients(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> new IngredientDto(ingredient.getId(), ingredient.getName(), ingredient.getAmount(), ingredient.getUnitOfMeasurement()))
                .collect(Collectors.toList());
    }



    public Recipe toEntity(RecipeRequestDto requestDto, String photo, User user) {
        String category = requestDto.category().toUpperCase();
        String difficulty = requestDto.difficulty().toUpperCase();
        Recipe recipe = new Recipe();
        recipe.setRecipeName(requestDto.recipeName());
        recipe.setCategory(Category.valueOf(category));
        recipe.setDifficulty(Difficulty.valueOf(difficulty));
        recipe.setDescription(requestDto.description());
        recipe.setPhotoUrl(photo);
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

    public List<RecipeSearchDto> toRecipeSearchListDto(List<Recipe> recipes) {
        return recipes.stream().map(recipe -> new RecipeSearchDto(recipe.getId(), recipe.getPhotoUrl(), recipe.getRecipeName())).collect(Collectors.toList());
    }
}
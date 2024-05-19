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

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    public RecipeListDto toRecipeListDto(Recipe recipe, boolean isLikedByUser, boolean isSavedByUser) {
        return new RecipeListDto(
                recipe.getId(),
                recipe.getPhoto(),
                recipe.getRecipeName(),
                recipe.getCreatedBy().getName(),
                recipe.getLikes().size(),
                recipe.getSaves().size(),
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
    public RecipeDto toRecipeDto(Recipe recipe, Long userId, boolean isLiked, boolean isSaved) {
        return new RecipeDto(
                recipe.getId(),
                recipe.getRecipeName(),
                recipe.getPhoto(),
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
                .map(ingredient -> new IngredientDto(ingredient.getId(), ingredient.getName(), ingredient.getAmount()))
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
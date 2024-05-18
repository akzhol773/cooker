package org.example.cookercorner.services.Impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.dtos.IngredientRequestDto;
import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.entities.Ingredient;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.enums.Difficulty;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.mapper.RecipeMapper;
import org.example.cookercorner.repositories.RecipeRepository;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.ImageService;
import org.example.cookercorner.services.RecipeService;
import org.example.cookercorner.services.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeServiceImpl implements RecipeService {

    RecipeRepository recipeRepository;
    UserService userService;
    RecipeMapper recipeMapper;
    ImageService imageService;


    @Override
    public List<RecipeListDto> getByCategory(Category categoryEnum, Long userId) {
        return recipeRepository.findByCategory(categoryEnum);
    }

    @Override
    public List<RecipeListDto> getMyRecipe(Long userId) {
        User user = userService.findUserById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes  = recipeRepository.findRecipesByCreatedBy(user);
        return recipeMapper.toRecipeListDtoList(recipes, user.getId());
    }

    @Override
    public List<RecipeListDto> getMySavedRecipe(Long userId) {
        User user = userService.findUserById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes = recipeRepository.findMySavedRecipes(user.getId());
       return recipeMapper.toRecipeListDtoList(recipes, user.getId());
    }

    @Override
    public RecipeDto getRecipeById(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        return recipeMapper.toRecipeDto(recipe, userId);
    }

    @Override
    public void addRecipe(RecipeRequestDto requestDto, MultipartFile image, Long userId) {
        Recipe recipe = new Recipe();
        recipe.setRecipeName(requestDto.recipeName());
        recipe.setCategory(Category.valueOf(requestDto.category().toUpperCase()));
        recipe.setDifficulty(Difficulty.valueOf(requestDto.difficulty().toUpperCase()));
        recipe.setDescription(requestDto.description());
        recipe.setPhoto(imageService.saveImage(image));
        recipe.setCookingTime(requestDto.cookingTime());
        User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        recipe.setCreatedBy(user);
        List<Ingredient> ingredients = new ArrayList<>();
        for(IngredientRequestDto ingredient: requestDto.ingredients()){
            Ingredient ingredient1 = new Ingredient();
            ingredient1.setRecipe(recipe);
            ingredient1.setName(ingredient.name());
            ingredient1.setAmount(ingredient.weight());
            ingredients.add(ingredient1);
        }
        recipe.setIngredients(ingredients);
        return recipeRepository.save(recipe);
    }

    @Override
    public List<RecipeListDto> searchRecipes(String recipe, Long currentUserId) {
        List<Recipe> recipes = recipeRepository.findRecipesByRecipeNameContainingIgnoreCase(recipe);
        return recipeMapper.toRecipeListDtoList(recipes, currentUserId);
    }

    @Override
    public boolean isLiked(Long recipeId, Long currentUserId) {
        return recipeRepository.isLikedByUser(recipeId, currentUserId);
    }

    @Override
    public Optional<Recipe> findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId);
    }

    @Override
    public void removeLikeFromRecipe(Long recipeId, Long currentUserId) {
        User user = userService.findUserById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        recipeRepository.removeLikeFromRecipe(recipe.getId(), user);
    }

    @Override
    public void putLikeIntoRecipe(Long recipeId, Long currentUserId) {
        User user = userService.findUserById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        recipeRepository.putLikeIntoRecipe(recipe.getId(), user.getId());
    }

    @Override
    public boolean isSaved(Long recipeId, Long currentUserId) {
        User user = userService.findUserById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        return recipeRepository.isSavedByUser(recipe.getId(), user.getId());
    }

    @Override
    public void removeSaveFromRecipe(Long recipeId, Long currentUserId) {
        User user = userService.findUserById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        recipeRepository.removeSaveFromRecipe(recipe.getId(), user.getId());
    }

    @Override
    public void putSaveIntoRecipe(Long recipeId, Long currentUserId) {
        User user = userService.findUserById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        recipeRepository.saveRecipeForUser(recipe.getId(), user.getId());
    }


}

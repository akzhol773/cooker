package org.example.cookercorner.services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.component.JsonValidator;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.dtos.IngredientRequestDto;
import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.entities.Ingredient;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.enums.Difficulty;
import org.example.cookercorner.exceptions.InvalidFileException;
import org.example.cookercorner.exceptions.InvalidJsonException;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.exceptions.UserNotFoundException;
import org.example.cookercorner.mapper.RecipeMapper;
import org.example.cookercorner.repositories.RecipeRepository;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.ImageService;
import org.example.cookercorner.services.RecipeService;
import org.example.cookercorner.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.rmi.ServerException;
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
    JwtTokenUtils jwtTokenUtils;
    ObjectMapper objectMapper;
    JsonValidator jsonValidator;


    @Override
    public List<RecipeListDto> getMyRecipe(Authentication authentication) {
        checkAuthentication(authentication);
       User user = getUserByAuthentication(authentication);
        return recipeMapper.toRecipeListDtoList(recipeRepository.findRecipesByCreatedBy(user), user.getId());
    }

    @Override
    public List<RecipeListDto> getMySavedRecipe(Authentication authentication) {
        checkAuthentication(authentication);
        User user = getUserByAuthentication(authentication);
       return recipeMapper.toRecipeListDtoList(recipeRepository.findMySavedRecipes(user.getId()), user.getId());
    }

    @Override
    public RecipeDto getRecipeById(Long recipeId, Authentication authentication) {
        checkAuthentication(authentication);
        return recipeMapper.toRecipeDto(getRecipe(recipeId), jwtTokenUtils.getUserIdFromAuthentication(authentication));
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
    public void removeLikeFromRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.removeLikeFromRecipe(getRecipe(recipeId).getId(), getUser(currentUserId));
    }

    @Override
    public void putLikeIntoRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.putLikeIntoRecipe(getRecipe(recipeId).getId(), getUser(currentUserId).getId());
    }

    @Override
    public boolean isSaved(Long recipeId, Long currentUserId) {
        return recipeRepository.isSavedByUser(getRecipe(recipeId).getId(), getUser(currentUserId).getId());
    }


    @Override
    public void removeSaveFromRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.removeSaveFromRecipe(getRecipe(recipeId).getId(), getUser(currentUserId).getId());
    }

    @Override
    public void putSaveIntoRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.saveRecipeForUser(getRecipe(recipeId).getId(), getUser(currentUserId).getId());
    }

    @Override
    public int getUserRecipeQuantity(User user) {
        return recipeRepository.getUserRecipeQuantity(user);
    }

    @Override
    public List<RecipeListDto> getByCategory(Authentication authentication, String category) {
       checkAuthentication(authentication);
        return recipeRepository.findByCategory(Category.valueOf(category.toUpperCase()));
    }

    @Override
    public String addRecipe(String recipeDto, MultipartFile image, Authentication authentication) throws FileUploadException {
        checkAuthentication(authentication);

        RecipeRequestDto requestDto = parseAndValidateRecipeDto(recipeDto);
        User user = getUserFromAuthentication(authentication);

        validateImage(image);
        String imagePath = saveImage(image);

        Recipe recipe = recipeMapper.toEntity(requestDto, imagePath, user);
        recipeRepository.save(recipe);

        return "The recipe has been added successfully!";
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty() || !imageService.isImageFile(image)) {
            throw new InvalidFileException("The file is not an image!");
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        Long userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        return userService.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private RecipeRequestDto parseAndValidateRecipeDto(String recipeDto) {
        try {
            RecipeRequestDto requestDto = objectMapper.readValue(recipeDto, RecipeRequestDto.class);
            jsonValidator.validateRecipeRequest(requestDto);
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }

    private String saveImage(MultipartFile image) throws FileUploadException {
        try {
            return imageService.saveImage(image);
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload image", e);
        }
    }

    private User getUserByAuthentication(Authentication authentication) {
        return userService.findUserById(jwtTokenUtils.getUserIdFromAuthentication(authentication)).orElseThrow(() ->
                new UserNotFoundException("User not found"));
    }

    private static void checkAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
    }

    private Recipe getRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
    }

    private User getUser(Long currentUserId) {
        return userService.findUserById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}

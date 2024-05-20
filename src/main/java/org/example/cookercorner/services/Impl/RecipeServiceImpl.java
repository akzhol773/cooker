package org.example.cookercorner.services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.component.JsonValidator;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.exceptions.InvalidFileException;
import org.example.cookercorner.exceptions.InvalidJsonException;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.exceptions.UserNotFoundException;
import org.example.cookercorner.mapper.RecipeMapper;
import org.example.cookercorner.repositories.RecipeRepository;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.ImageService;
import org.example.cookercorner.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeServiceImpl implements RecipeService {
    RecipeRepository recipeRepository;
    UserRepository userRepository;
    RecipeMapper recipeMapper;
    ImageService imageService;
    JwtTokenUtils jwtTokenUtils;
    ObjectMapper objectMapper;
    JsonValidator jsonValidator;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository, RecipeMapper recipeMapper, ImageService imageService, JwtTokenUtils jwtTokenUtils, ObjectMapper objectMapper, JsonValidator jsonValidator) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeMapper = recipeMapper;
        this.imageService = imageService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.objectMapper = objectMapper;
        this.jsonValidator = jsonValidator;
    }


    @Override
    public List<RecipeListDto> getMyRecipe(Authentication authentication) {
        checkAuthentication(authentication);
       User user = getUserByAuthentication(authentication);
       List<Recipe> recipes = recipeRepository.findRecipesByCreatedBy(user);
        return recipes.stream()
                .map(recipe -> {
                    boolean isLikedByUser = isLikedByUser(recipe.getId(), user.getId());
                    boolean isSavedByUser = isSavedByUser(recipe.getId(), user.getId());
                    return recipeMapper.toRecipeListDto(recipe, isLikedByUser, isSavedByUser);
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<RecipeListDto> getMySavedRecipe(Authentication authentication) {
        checkAuthentication(authentication);
        User user = getUserByAuthentication(authentication);

        List<Recipe> recipes = recipeRepository.findMySavedRecipes(user.getId());
        return recipes.stream()
                .map(recipe -> {
                    boolean isLikedByUser = recipeRepository.isLikedByUser(recipe.getId(), user.getId());
                    boolean isSavedByUser = recipeRepository.isSavedByUser(recipe.getId(), user.getId());
                    return recipeMapper.toRecipeListDto(recipe, isLikedByUser, isSavedByUser);
                })
                .collect(Collectors.toList());
    }

    @Override
    public RecipeDto getRecipeById(Long recipeId, Authentication authentication) {
        checkAuthentication(authentication);
        User user = getUser(jwtTokenUtils.getUserIdFromAuthentication(authentication));
        boolean isSaved = recipeRepository.isRecipeSavedByUser(recipeId, user.getId());
        boolean isLiked = recipeRepository.isRecipeLikedByUser(recipeId, user.getId());
        return recipeMapper.toRecipeDto(getRecipe(recipeId), isLiked, isSaved);
    }



    @Override
    public List<RecipeSearchDto> searchRecipes(String recipe, Authentication authentication) {
        checkAuthentication(authentication);
        List<Recipe> recipes = recipeRepository.findRecipesByRecipeNameContainingIgnoreCase(recipe);
        return recipeMapper.toRecipeSearchListDto(recipes);
    }

    @Override
    public int getUserRecipeQuantity(User user) {
        return recipeRepository.getUserRecipeQuantity(user);
    }

    @Override
    public List<RecipeListDto> getByCategory(Authentication authentication, String category) {
       checkAuthentication(authentication);
       Long currentUserId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        List<Recipe> recipes = recipeRepository.findByCategory(Category.valueOf(category.toUpperCase()));
        return recipes.stream().map(recipe -> recipeMapper.toRecipeListDto(recipe,
                isLikedByUser(recipe.getId(), currentUserId),
                isSavedByUser(recipe.getId(), currentUserId))).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String addRecipe(String recipeDto, MultipartFile image, Authentication authentication){
        checkAuthentication(authentication);

        RecipeRequestDto requestDto = parseAndValidateRecipeDto(recipeDto);
        User user = getUserFromAuthentication(authentication);

        validateImage(image);
        String imageUrl = saveImage(image);

        Recipe recipe = recipeMapper.toEntity(requestDto, imageUrl, user);
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
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
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

    private String saveImage(MultipartFile image){
        return imageService.uploadFile(image, "recipe_photos");
    }

    private User getUserByAuthentication(Authentication authentication) {
        return userRepository.findById(jwtTokenUtils.getUserIdFromAuthentication(authentication)).orElseThrow(() ->
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
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private boolean isSavedByUser(Long recipeId, Long userId) {
        return recipeRepository.isSavedByUser(recipeId, userId);
    }

    private boolean isLikedByUser(Long recipeId, Long userId) {
        return recipeRepository.isLikedByUser(recipeId, userId);
    }

}

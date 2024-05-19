package org.example.cookercorner.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.component.JsonValidator;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.services.ImageService;
import org.example.cookercorner.services.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/recipes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeController {

    JwtTokenUtils jwtTokenUtils;
    RecipeService recipeService;
    ObjectMapper objectMapper;
    JsonValidator jsonValidator;
    ImageService imageService;
    @Operation(
            summary = "Get recipes by category",
            description = "Using this endpoint it is possible to get recipes by category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/get-by-category")
    public ResponseEntity<List<RecipeListDto>> getRecipes(@RequestParam(value = "category") String category,
                                                          Authentication authentication) {
       return ResponseEntity.ok(recipeService.getByCategory(authentication, category));
    }


    @Operation(
            summary = "Get recipes of the current user",
            description = "Using this endpoint it is possible to get recipes of the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/my-recipes")
    public ResponseEntity<List<RecipeListDto>> getMyRecipes(Authentication authentication) {
        return ResponseEntity.ok(recipeService.getMyRecipe(authentication));
    }


    @Operation(
            summary = "Get saved recipes of the current user",
            description = "Using this endpoint it is possible to get saved recipes o the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )

    @GetMapping("/my-saved-recipes")
    public ResponseEntity<List<RecipeListDto>> getMySavedRecipes(Authentication authentication) {
        return ResponseEntity.ok(recipeService.getMySavedRecipe(authentication));
    }




    @Operation(
            summary = "Get detailed page of the recipe",
            description = "Using this endpoint it is possible to get detailed recipe",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )

    @GetMapping("/get-by-id/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long recipeId, Authentication authentication) {
       return ResponseEntity.ok(recipeService.getRecipeById(recipeId, authentication));
    }


    @Operation(
            summary = "Add recipe",
            description = "Whenever user wants to create a new recipe then he or she should to use this endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe successfully created"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @PostMapping("/add_recipe")
    public ResponseEntity<String> addRecipe(@RequestPart("recipeDto") String recipeDto,
                                            @RequestPart("photo") MultipartFile image,
                                            Authentication authentication) throws FileUploadException {
     return ResponseEntity.ok(recipeService.addRecipe(recipeDto, image, authentication));
    }




    @Operation(
            summary = "Search recipe",
            description = "Search recipes based on user query ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/search-recipe")
    public ResponseEntity<List<RecipeListDto>> search(@RequestParam(name = "query")String query, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(recipeService.searchRecipes(query, userId));
    }
}

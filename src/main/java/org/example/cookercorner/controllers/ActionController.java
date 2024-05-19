package org.example.cookercorner.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.services.ActionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("api/actions")
public class ActionController {

    ActionService actionService;

    @PutMapping("/like/{recipeId}")
    @Operation(summary = "Toggle like for a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like status liked successfully"),
            @ApiResponse(responseCode = "200", description = "Like status unliked successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<String> toggleLike(Authentication authentication,
                                             @PathVariable(name = "recipeId") Long recipeId) {

        return ResponseEntity.ok(actionService.toggleLike(authentication, recipeId));

    }


    @PutMapping("/save/{recipeId}")
    @Operation(summary = "Toggle mark for a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mark status toggled successfully"),
            @ApiResponse(responseCode = "200", description = "Mark status toggled back successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<String> toggleSave(Authentication authentication,
                                             @PathVariable(name = "recipeId") Long recipeId) {

        return ResponseEntity.ok(actionService.toggleSave(authentication, recipeId));

    }


    @PutMapping("/follow/{userId}")
    @Operation(summary = "Toggle follow for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User followed successfully"),
            @ApiResponse(responseCode = "200", description = "User unfollowed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<String> toggleFollow(Authentication authentication, @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(actionService.toggleFollow(authentication, userId));
    }

}



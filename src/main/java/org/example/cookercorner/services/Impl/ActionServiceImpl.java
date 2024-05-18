package org.example.cookercorner.services.Impl;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.exceptions.NotAuthorizedException;
import org.example.cookercorner.services.ActionService;
import org.example.cookercorner.services.RecipeService;
import org.example.cookercorner.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActionServiceImpl implements ActionService {
    JwtTokenUtils jwtTokenUtils;
    RecipeService recipeService;
    UserService userService;
    @Override
    public String toggleLike(Authentication authentication, Long recipeId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthorizedException("Authentication required!");
        }
        Long currentUserId = jwtTokenUtils.getUserIdFromAuthentication(authentication);

        if (recipeService.isLiked(recipeId, currentUserId)) {
            recipeService.removeLikeFromRecipe(recipeId, currentUserId);
            return "Like removed successfully";
        }
            recipeService.putLikeIntoRecipe(recipeId, currentUserId);
            return "Like added successfully";

    }

    @Override
    public String toggleSave(Authentication authentication, Long recipeId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthorizedException("Authentication required!");
        }
        Long currentUserId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        if (recipeService.isSaved(recipeId, currentUserId)) {
            recipeService.removeSaveFromRecipe(recipeId, currentUserId);
            return "Recipe unsaved successfully!";
        }
            recipeService.putSaveIntoRecipe(recipeId, currentUserId);
            return "Recipe saved successfully!";
    }

    @Override
    public String toggleFollow(Authentication authentication, Long userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthorizedException("Authentication required!");
        }
       Long currentUserId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        if (userService.isFollowed(userId, currentUserId)) {
             userService.unfollowUser(userId, currentUserId);
            return "Unfollowed successfully";
        }
            userService.followUser(userId, currentUserId);
            return "Followed successfully";
    }


}






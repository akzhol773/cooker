package org.example.cookercorner.services.Impl;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.exceptions.NotAuthorizedException;
import org.example.cookercorner.repositories.RecipeRepository;
import org.example.cookercorner.services.ActionService;
import org.example.cookercorner.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActionServiceImpl implements ActionService {

    JwtTokenUtils jwtTokenUtils;
    RecipeRepository recipeRepository;
    UserService userService;

    @Override
    @Transactional
    public String toggleLike(Authentication authentication, Long recipeId) {
        checkAuthentication(authentication);
        Long currentUserId = getUserIdByAuthentication(authentication);
        if (isLiked(recipeId, currentUserId)) {
            removeLikeFromRecipe(recipeId, currentUserId);
            return "Like removed successfully";
        }
            putLikeIntoRecipe(recipeId, currentUserId);
            return "Like added successfully";
    }


    @Override
    @Transactional
    public String toggleSave(Authentication authentication, Long recipeId) {
        checkAuthentication(authentication);
        Long currentUserId = getUserIdByAuthentication(authentication);
        if (isSaved(recipeId, currentUserId)) {
            removeSaveFromRecipe(recipeId, currentUserId);
            return "Recipe unsaved successfully!";
        }
            putSaveIntoRecipe(recipeId, currentUserId);
            return "Recipe saved successfully!";
    }

    @Override
    @Transactional
    public String toggleFollow(Authentication authentication, Long userId) {
        checkAuthentication(authentication);
       Long currentUserId = getUserIdByAuthentication(authentication);
       if(currentUserId == userId){
           return "User can not follow yourself!";
       }
        if (userService.isFollowed(userId, currentUserId)) {
             userService.unfollowUser(userId, currentUserId);
            return "Unfollowed successfully";
        }
            userService.followUser(userId, currentUserId);
            return "Followed successfully";
    }

    private static void checkAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthorizedException("Authentication required!");
        }
    }
    private Long getUserIdByAuthentication(Authentication authentication) {
        return jwtTokenUtils.getUserIdFromAuthentication(authentication);
    }

    private boolean isSaved(Long recipeId, Long currentUserId) {
        return recipeRepository.isSavedByUser(recipeId, currentUserId);
    }


    private boolean isLiked(Long recipeId, Long currentUserId) {
        return recipeRepository.isLikedByUser(recipeId, currentUserId);
    }

    private void removeLikeFromRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.removeLikeFromRecipe(recipeId, currentUserId);
    }

    private void putLikeIntoRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.putLikeIntoRecipe(recipeId, currentUserId);
    }

    private void removeSaveFromRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.removeSaveFromRecipe(recipeId, currentUserId);
    }

    private void putSaveIntoRecipe(Long recipeId, Long currentUserId) {
        recipeRepository.saveRecipeForUser(recipeId, currentUserId);
    }


}






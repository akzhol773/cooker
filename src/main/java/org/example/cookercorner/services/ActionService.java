package org.example.cookercorner.services;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


public interface ActionService {
    String toggleLike(Authentication authentication, Long recipeId);

    String toggleSave(Authentication authentication, Long recipeId);

    String toggleFollow(Authentication authentication, Long userId);
}

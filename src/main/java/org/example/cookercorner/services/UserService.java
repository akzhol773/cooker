package org.example.cookercorner.services;

import org.example.cookercorner.entities.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long currentUserId);

    boolean isFollowed(Long userId, Long currentUserId);

    void unfollowUser(Long userId, Long currentUserId);

    void followUser(Long userId, Long currentUserId);
}

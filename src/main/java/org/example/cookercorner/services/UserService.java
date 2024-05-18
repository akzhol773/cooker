package org.example.cookercorner.services;

import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserDto;
import org.example.cookercorner.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long currentUserId);

    boolean isFollowed(Long userId, Long currentUserId);

    void unfollowUser(Long userId, Long currentUserId);

    void followUser(Long userId, Long currentUserId);

    List<UserDto> searchUser(String query);

    MyProfileDto getMyProfile(Authentication authentication);
}

package org.example.cookercorner.services.Impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserById(Long currentUserId) {
        return userRepository.findById(currentUserId);
    }

    @Override
    public boolean isFollowed(Long userId, Long currentUserId) {
        return userRepository.isFollowedByUser(userId, currentUserId);
    }

    @Override
    public void unfollowUser(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User to unfollow not found"));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        // Remove the follow relationship
        int followingsDeleted = userRepository.unfollowUser(currentUserId, userId);
        if (followingsDeleted == 0) {
            throw new IllegalStateException("User was not being followed.");
        }

        // Remove the follower relationship
        int followersDeleted = userRepository.removeFollower(userId, currentUserId);
        if (followersDeleted == 0) {
            throw new IllegalStateException("Current user was not a follower of the user to unfollow.");
        }

    }
}

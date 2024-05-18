package org.example.cookercorner.services.Impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserDto;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.exceptions.NotAuthorizedException;
import org.example.cookercorner.exceptions.UserNotFoundException;
import org.example.cookercorner.mapper.UserMapper;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.RecipeService;
import org.example.cookercorner.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    JwtTokenUtils jwtTokenUtils;
    RecipeService recipeService;

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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userRepository.isFollowedByUser(user.getId(), currentUser.getId());
    }

    @Override
    @Transactional
    public void unfollowUser(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
       userRepository.unfollowUser(currentUser.getId(), user.getId());
       userRepository.removeFollower(user.getId(), currentUser.getId());

    }

    @Override
    @Transactional
    public void followUser(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.followUser(currentUser.getId(), user.getId());
        userRepository.addFollower(user.getId(), currentUser.getId());

    }

    @Override
    public List<UserDto> searchUser(String query) {
        List<User> users = userRepository.searchUsers(query);
        return userMapper.toListUser(users);
    }

    @Override
    public MyProfileDto getMyProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthorizedException("Authentication required!");
        }
        Long currentUserId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        User user = userRepository.findById(currentUserId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        String photoUrl = (user.getPhoto() != null) ? user.getPhoto() : "https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg";
        return userMapper.toMyProfileDto(user, photoUrl, recipeService.getUserRecipeQuantity(user));
    }
}






























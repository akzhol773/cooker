package org.example.cookercorner.services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.component.JsonValidator;
import org.example.cookercorner.component.JwtTokenUtils;
import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserSearchDto;
import org.example.cookercorner.dtos.UserProfileDto;
import org.example.cookercorner.dtos.UserUpdateProfileDto;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.exceptions.InvalidJsonException;
import org.example.cookercorner.exceptions.NotAuthorizedException;
import org.example.cookercorner.exceptions.UserNotFoundException;
import org.example.cookercorner.mapper.UserMapper;
import org.example.cookercorner.repositories.UserRepository;
import org.example.cookercorner.services.ImageService;
import org.example.cookercorner.services.RecipeService;
import org.example.cookercorner.services.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    JwtTokenUtils jwtTokenUtils;
    RecipeService recipeService;
    ObjectMapper objectMapper;
    JsonValidator jsonValidator;
    ImageService imageService;

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
        User user =  getUser(userId);
        User currentUser = getUser(currentUserId);
        return userRepository.isFollowedByUser(user.getId(), currentUser.getId());
    }



    @Override
    @Transactional
    public void unfollowUser(Long userId, Long currentUserId) {
        User user = getUser(userId);
        User currentUser =getUser(currentUserId);
       userRepository.unfollowUser(currentUser.getId(), user.getId());
       userRepository.removeFollower(user.getId(), currentUser.getId());

    }

    @Override
    @Transactional
    public void followUser(Long userId, Long currentUserId) {
        User user = getUser(userId);
        User currentUser = getUser(currentUserId);
        userRepository.followUser(currentUser.getId(), user.getId());
        userRepository.addFollower(user.getId(), currentUser.getId());

    }

    @Override
    public List<UserSearchDto> searchUser(String query) {
        List<User> users = userRepository.searchUsers(query);
        return userMapper.toListUser(users);
    }

    @Override
    public MyProfileDto getMyProfile(Authentication authentication) {
        checkAuthentication(authentication);
        User user = getUserFromAuthentication(authentication);
        return userMapper.toMyProfileDto(user, recipeService.getUserRecipeQuantity(user));
    }



    @Override
    @Transactional
    public String updateProfile(String profileDto, MultipartFile image, Authentication authentication) throws FileUploadException {
        checkAuthentication(authentication);
        User user = getUserFromAuthentication(authentication);
        UserUpdateProfileDto request = parseAndValidateProfileDto(profileDto);
        validateImage(image);
        updateUserInfo(user, request, image);
        userRepository.save(user);
        return "User profile successfully updated";
    }

    @Override
    public UserProfileDto getUserProfileById(Long userId, Authentication authentication) {
        checkAuthentication(authentication);
        User currentUser = getUser(jwtTokenUtils.getUserIdFromAuthentication(authentication));
        User user = getUser(userId);
        boolean isFollowing = userRepository.isUserFollowing(currentUser.getId(), userId);
        return userMapper.toUserProfileDto(user, recipeService.getUserRecipeQuantity(user), isFollowing);
    }

    private void validateImage(MultipartFile image) {
        if (image != null && !imageService.isImageFile(image)) {
            throw new BadCredentialsException("The file is not an image");
        }
    }

    private void updateUserInfo(User user, UserUpdateProfileDto request, MultipartFile image){
        user.setName(request.name());
        user.setBiography(request.biography());
        if (image != null) {
            user.setPhotoUrl(imageService.uploadFile(image, "user_photos"));
        }
    }


    private UserUpdateProfileDto parseAndValidateProfileDto(String profileDto) {
        try {
            UserUpdateProfileDto request = objectMapper.readValue(profileDto, UserUpdateProfileDto.class);
            jsonValidator.validateUserRequest(request);
            return request;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid JSON format: " + e.getMessage());
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findById(jwtTokenUtils.getUserIdFromAuthentication(authentication)).orElseThrow(()-> new UserNotFoundException("User not found"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void checkAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthorizedException("Authentication required!");
        }
    }
}





























package org.example.cookercorner.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserSearchDto;
import org.example.cookercorner.dtos.UserProfileDto;
import org.example.cookercorner.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String email);

    boolean isFollowed(Long userId, Long currentUserId);

    void unfollowUser(Long userId, Long currentUserId);

    void followUser(Long userId, Long currentUserId);

    List<UserSearchDto> searchUser(String query);

    MyProfileDto getMyProfile(Authentication authentication);

    String updateProfile(String profileDto, MultipartFile image, Authentication authentication) throws FileUploadException;

    UserProfileDto getUserProfileById(Long userId, Authentication authentication);
}

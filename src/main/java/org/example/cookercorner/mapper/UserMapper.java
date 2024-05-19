package org.example.cookercorner.mapper;

import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserSearchDto;
import org.example.cookercorner.dtos.UserProfileDto;
import org.example.cookercorner.dtos.UserRequestDto;
import org.example.cookercorner.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto dto, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(dto.password()));
        return user;
    }

    public List<UserSearchDto> toListUser(List<User> users) {
        return users.stream().map(user ->
                new UserSearchDto(user.getId(), user.getUsername(),
                        user.getPhoto())).collect(Collectors.toList());
    }


    public MyProfileDto toMyProfileDto(User user, int userRecipeQuantity) {

       return new MyProfileDto(
               user.getPhoto(),
                user.getName(),
                userRecipeQuantity,
                user.getFollowers().size(),
                user.getFollowings().size(),
                user.getBiography()
        );
    }

    public UserProfileDto toUserProfileDto(User user, int recipeQuantity, boolean isFollowing) {
       return new UserProfileDto(
               user.getId(),
               user.getPhoto(),
               user.getUsername(),
               recipeQuantity,
               user.getFollowers().size(),
               user.getFollowings().size(),
               user.getBiography(),
               isFollowing
       );
    }
}

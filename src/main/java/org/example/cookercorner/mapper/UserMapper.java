package org.example.cookercorner.mapper;

import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserDto;
import org.example.cookercorner.dtos.UserRequestDto;
import org.example.cookercorner.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto dto, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setRole("USER_ROLE");
        user.setPassword(passwordEncoder.encode(dto.password()));
        return user;
    }

    public List<UserDto> toListUser(List<User> users) {
        List<UserDto> userDto = new ArrayList<>();
        String defaultPhotoPath = "path/to/your/default/photo.jpg"; // Change this to your actual default file path

        for (User user : users) {
            String photoUrl = user.getPhoto();
            if (photoUrl == null || photoUrl.isEmpty()) {
                photoUrl = defaultPhotoPath;
            }
            UserDto dto = new UserDto(
                    user.getId(),
                    user.getName(),
                    photoUrl
            );
            userDto.add(dto);
        }
        return userDto;
    }


    public MyProfileDto toMyProfileDto(User user, String photoUrl, int userRecipeQuantity) {

       return new MyProfileDto(
                photoUrl,
                user.getName(),
                userRecipeQuantity,
                user.getFollowers().size(),
                user.getFollowings().size(),
                user.getBiography()
        );
    }
}

package org.example.cookercorner.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserDto;
import org.example.cookercorner.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@RequestMapping("api/users/")
public class UserController {
    UserService userService;

    @Operation(
            summary = "Search user",
            description = "Search users based on user name query",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users list"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/search-user")
    public ResponseEntity<List<UserDto>> search(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(userService.searchUser(query));
    }


    @Operation(
            summary = "Get own profile",
            description = "User can get own profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profile"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/my_profile")
    public ResponseEntity<MyProfileDto> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyProfile(authentication));
    }


    @Operation(
            summary = "Update profile",
            description = "Using this endpoint user can update his or her profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @PutMapping("/update_profile")
    public ResponseEntity<String> changeProfile(@RequestPart("dto") String profileDto,
                                                @RequestPart(value = "image", required = false) MultipartFile image,
                                                Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }
            UserUpdateProfileDto request = objectMapper.readValue(profileDto, UserUpdateProfileDto.class);
            jsonValidator.validateUserRequest(request);

            if (image != null && !isImageFile(image)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The file is not an image");
            }
            Long currentUserId = tokenUtils.getUserIdFromAuthentication(authentication);
            String responseMessage = userService.updateUser(request, currentUserId, image);
            return ResponseEntity.ok(responseMessage);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid profile DTO JSON: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile: " + e.getMessage());
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}

package org.example.cookercorner.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.cookercorner.dtos.MyProfileDto;
import org.example.cookercorner.dtos.UserDto;
import org.example.cookercorner.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
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
                                                Authentication authentication) throws FileUploadException {
       return ResponseEntity.ok(userService.updateProfile(profileDto, image, authentication));
    }

}

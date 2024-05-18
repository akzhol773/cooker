package org.example.cookercorner.dtos;

import lombok.Builder;

import java.io.Serializable;


@Builder
public record UserProfileDto(Long id, String imageUrl, String name, int recipeQuantity, int followerQuantity, int followingQuantity, String biography, boolean isFollowed) implements Serializable {

}
package org.example.cookercorner.repositories;

import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("select r from Recipe r where r.category = :category ORDER BY SIZE(r.likes) DESC limit 10")
    List<RecipeListDto> findByCategory(@Param("category") Category category);

    List<Recipe> findRecipesByCreatedBy(User createdBy);

    @Query("SELECT r FROM Recipe r JOIN r.saves s WHERE s = :userId")
    List<Recipe> findMySavedRecipes(Long id);

    List<Recipe> findRecipesByRecipeNameContainingIgnoreCase(String query);

    @Query("SELECT COUNT(r) > 0 FROM Recipe r JOIN r.likes u WHERE r.id = :recipeId AND u = :userId")
    boolean isLikedByUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Recipe r WHERE r.id = :recipeId AND :user MEMBER OF r.likes")
    void removeLikeFromRecipe(@Param("recipeId") Long recipeId, @Param("user") User user);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO recipe_likes (recipe_id, user_id) VALUES (:recipeId, :userId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void putLikeIntoRecipe(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(rs) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Recipe r JOIN r.saves rs " +
            "WHERE r.id = :recipeId AND rs = :userId")
    boolean isSavedByUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM recipe_saves WHERE recipe_id = :recipeId AND user_id = :userId", nativeQuery = true)
    void removeSaveFromRecipe(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO recipe_saves (recipe_id, user_id) VALUES (:recipeId, :userId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void saveRecipeForUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);


    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.createdBy = :user")
    int getUserRecipeQuantity(@Param("user") User user);
}



























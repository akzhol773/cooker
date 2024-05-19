package org.example.cookercorner.repositories;

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
    List<Recipe> findByCategory(@Param("category") Category category);

    List<Recipe> findRecipesByCreatedBy(User createdBy);

    @Query("SELECT r FROM Recipe r JOIN r.saves s WHERE s = :userId")
    List<Recipe> findMySavedRecipes(@Param("userId") Long userId);

    List<Recipe> findRecipesByRecipeNameContainingIgnoreCase(String query);

    @Query("SELECT COUNT(r) > 0 FROM Recipe r JOIN r.likes u WHERE r.id = :recipeId AND u = :userId")
    boolean isLikedByUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM recipe_likes WHERE recipe_id = :recipeId AND likes = :userId", nativeQuery = true)
    void removeLikeFromRecipe(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO recipe_likes (recipe_id, likes) VALUES (:recipeId, :userId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void putLikeIntoRecipe(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(rs) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Recipe r JOIN r.saves rs " +
            "WHERE r.id = :recipeId AND rs = :userId")
    boolean isSavedByUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM recipe_saves WHERE recipe_id = :recipeId AND saves = :userId", nativeQuery = true)
    void removeSaveFromRecipe(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO recipe_saves (recipe_id, saves) VALUES (:recipeId, :userId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void saveRecipeForUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);


    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.createdBy = :user")
    int getUserRecipeQuantity(@Param("user") User user);


    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM Recipe r JOIN r.likes l " +
            "WHERE r.id = :recipeId AND l = :userId")
    boolean isRecipeLikedByUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Recipe r JOIN r.saves s " +
            "WHERE r.id = :recipeId AND s = :userId")
    boolean isRecipeSavedByUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);
}



























package org.example.cookercorner.repositories;

import org.example.cookercorner.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM User u JOIN u.followings f " +
            "WHERE u.id = :currentUserId AND f = :userId")
    boolean isUserFollowing(@Param("currentUserId") Long currentUserId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_followings WHERE user_id = :currentUserId AND followings = :userId", nativeQuery = true)
    void unfollowUser(@Param("currentUserId") Long currentUserId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_followers WHERE user_id = :userId AND followers = :currentUserId", nativeQuery = true)
    void removeFollower(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_followings (user_id, followings) VALUES (:currentUserId, :userId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void followUser(@Param("currentUserId") Long currentUserId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_followers (user_id, followers) VALUES (:userId, :currentUserId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addFollower(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);


    List<User> findUsersByNameContainingIgnoreCase(String username);



}
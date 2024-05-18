package org.example.cookercorner.repositories;

import org.example.cookercorner.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM User u JOIN u.followings f " +
            "WHERE u.id = :currentUserId AND f = :userId")
    boolean isFollowedByUser(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_followings WHERE user_id = :currentUserId AND following_id = :userId", nativeQuery = true)
    boolean unfollowUser(@Param("currentUserId") Long currentUserId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_followers WHERE user_id = :userId AND follower_id = :currentUserId", nativeQuery = true)
    boolean removeFollower(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);

}
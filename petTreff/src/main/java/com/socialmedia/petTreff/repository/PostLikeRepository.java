package com.socialmedia.petTreff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.socialmedia.petTreff.entity.PostLike;
import com.socialmedia.petTreff.entity.User;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    long countByPost_Id(Long postId);

    // testet, ob ein Like existiert
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    Optional<PostLike> findByUser_IdAndPost_Id(Long userId, Long postId);

    // gibt alle Users, die ein Post like
    @Query("select l.user from PostLike l where l.post.id = :postId")
    List<User> findUsersWhoLikedPost(@Param("postId") Long postId);
}

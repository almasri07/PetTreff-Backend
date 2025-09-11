package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        SELECT p FROM Post p
        WHERE p.author.id = :userId
        OR p.author.id IN :friendIds
        """)
    Page<Post> findOwnAndFriendsPosts(@Param("userId") Long userId,
                                      @Param("friendIds") Collection<Long> friendIds,
                                      Pageable pageable);
}

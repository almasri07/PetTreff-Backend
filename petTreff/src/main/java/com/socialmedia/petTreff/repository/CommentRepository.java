package com.socialmedia.petTreff.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.socialmedia.petTreff.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // dies , um ein Comment zu finden
    Optional<Comment> findByIdAndPostId(Long commnetId, Long postId);

    // Page of comments for a post (paginated)
    Page<Comment> findByPostId(Long postId, Pageable pageable);

}

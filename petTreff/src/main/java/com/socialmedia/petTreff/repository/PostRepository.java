package com.socialmedia.petTreff.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialmedia.petTreff.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}

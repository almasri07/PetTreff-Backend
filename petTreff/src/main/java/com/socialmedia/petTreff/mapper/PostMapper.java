package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.CreatePostDTO;
import com.socialmedia.petTreff.dto.PostDTO;
import com.socialmedia.petTreff.dto.UpdatePostDTO;
import com.socialmedia.petTreff.entity.Post;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.repository.UserRepository;

import java.time.LocalDateTime;

public class PostMapper {

    private PostMapper() {
        // Private constructor to prevent instantiation
    }

    public static PostDTO toDto(Post post) {
        if (post == null) {
            return null;
        }
        PostDTO postDTO = new PostDTO();
        postDTO.setImageUrl(post.getImageUrl());
        postDTO.setId(post.getId());
        postDTO.setContent(post.getContent());
        postDTO.setAuthorUsername(post.getAuthor().getUsername());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setLikeCount(post.getLikeCount());
        postDTO.setUpdatedAt(post.getUpdatedAt());
        postDTO.setVersion(post.getVersion());
        postDTO.setAuthorId(post.getAuthor().getId());
        postDTO.setCaption(post.getCaption());
        postDTO.setFeeling(post.getFeeling());
        postDTO.setLocation(post.getLocation());
        return postDTO;
    }

    public static Post toEntity(PostDTO postDTO, UserRepository userRepository) {
        if (postDTO == null) {
            return null;
        }
        Post post = new Post();
        post.setId(postDTO.getId());
        post.setContent(postDTO.getContent());
        post.setAuthor(userRepository.getReferenceById(postDTO.getAuthorId()));
        post.setCreatedAt(postDTO.getCreatedAt());
        post.setVersion(postDTO.getVersion());
        post.setImageUrl(postDTO.getImageUrl());
        post.setLikeCount(postDTO.getLikeCount());
        post.setCaption(postDTO.getCaption());
        post.setFeeling(postDTO.getFeeling());
        post.setLocation(postDTO.getLocation());
        post.setCreatedAt(postDTO.getCreatedAt());
        post.setUpdatedAt(postDTO.getUpdatedAt());

        return post;
    }

    public static Post fromCreatePostDTO(CreatePostDTO req, User author) {
        if (req == null || author == null) {
            return null;
        }

        Post post = new Post();

        post.setContent(req.getContent());
        post.setImageUrl(req.getImageUrl());
        post.setAuthor(author);
        post.setLikeCount(0);
        post.setCaption(req.getCaption());
        post.setFeeling(req.getFeeling());
        post.setLocation(req.getLocation());
        post.setCreatedAt(LocalDateTime.now());

        return post;
    }

    public static void applyUpdates(Post post, UpdatePostDTO req) {
        if (post == null || req == null) {
            return;
        }

        if (req.getContent() != null) {
            post.setContent(req.getContent());
        }

        if (req.getImageUrl() != null) {
            post.setImageUrl(req.getImageUrl());
        }
        if (req.getFeeling() != null) {
            post.setFeeling(req.getFeeling());
        }
        if (req.getLocation() != null) {
            post.setLocation(req.getLocation());
        }
        if (req.getCaption() != null) {
            post.setCaption(req.getCaption());
        }
        post.setUpdatedAt(LocalDateTime.now());
    }

}

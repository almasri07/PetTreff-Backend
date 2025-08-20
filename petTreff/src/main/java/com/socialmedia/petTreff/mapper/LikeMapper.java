package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.LikeDTO;
import com.socialmedia.petTreff.entity.Post;
import com.socialmedia.petTreff.entity.PostLike;
import com.socialmedia.petTreff.entity.User;

public class LikeMapper {

    public static LikeDTO toDto(PostLike like) {
        if (like == null) {
            return null;
        }
        LikeDTO likeDTO = new LikeDTO();
        likeDTO.setId(like.getId());
        likeDTO.setPostId(like.getPost().getId());
        likeDTO.setUserId(like.getUser().getId());
        return likeDTO;
    }

    public static PostLike toEntity(LikeDTO likeDTO, User user, Post post) {
        if (likeDTO == null) {
            return null;
        }
        PostLike like = new PostLike();

        like.setId(likeDTO.getId());

        like.setPost(post);
        like.setUser(user);

        return like;
    }

}
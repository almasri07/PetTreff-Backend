package com.socialmedia.petTreff.dto;

import lombok.Data;

@Data
public class LikeDTO {

    private Long id;
    private Long userId;
    private Long postId;

}

package com.socialmedia.petTreff.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt, updatedAt;
    private Long version;
    private String authorUsername;

}

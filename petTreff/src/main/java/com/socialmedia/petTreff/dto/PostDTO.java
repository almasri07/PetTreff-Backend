package com.socialmedia.petTreff.dto;

import lombok.Data;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

@Data
public class PostDTO {

    private Long authorId;
    private String authorUsername;
    private Long id;

    @Size(max = 1000)
    private String content;
    @Size(max = 3000)
    private String imageUrl;
    @Size(max = 100)
    private String Feeling;
    @Size(max = 100)
    private String Location;
    @Size(max = 100)
    private String Caption;
    private int likeCount;
    private LocalDateTime createdAt, updatedAt;
    private Long version;

}
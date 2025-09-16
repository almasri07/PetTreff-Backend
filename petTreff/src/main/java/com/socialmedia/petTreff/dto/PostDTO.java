package com.socialmedia.petTreff.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

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
    private String feeling;
    @Size(max = 100)
    private String location;
    @Size(max = 100)
    private String caption;
    private int likeCount;
    private LocalDateTime createdAt, updatedAt;
    private Long version;

}
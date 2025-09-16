package com.socialmedia.petTreff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostDTO {

    @NotBlank
    private String content;
    private String imageUrl;
    private String feeling;
    private String location;
    private String caption;

}

package com.socialmedia.petTreff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostDTO {

    @NotBlank
    private String content;
    private String imageUrl;
    private String Feeling;
    private String Location;
    private String Caption;

}

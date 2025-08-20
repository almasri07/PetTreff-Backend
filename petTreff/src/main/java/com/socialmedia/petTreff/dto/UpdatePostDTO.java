package com.socialmedia.petTreff.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePostDTO {

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
    private LocalDateTime createdAt, updatedAt;

}

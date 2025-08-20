package com.socialmedia.petTreff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCommentDTO {

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 700, message = "Content must be at most 700 characters and at least 1 character long")
    private String content;

    private Long version;

}

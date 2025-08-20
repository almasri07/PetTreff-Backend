package com.socialmedia.petTreff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDTO {
    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 8, max = 120)
    private String newPassword;

}

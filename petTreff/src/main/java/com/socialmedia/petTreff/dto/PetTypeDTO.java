package com.socialmedia.petTreff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PetTypeDTO {

    private Long id;

    @NotBlank
    @Size(max = 40)
    private String typeName;

}

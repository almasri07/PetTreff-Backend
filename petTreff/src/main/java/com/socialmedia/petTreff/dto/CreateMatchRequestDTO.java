package com.socialmedia.petTreff.dto;

import com.socialmedia.petTreff.entity.PetTypeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMatchRequestDTO {


    @NotNull
    private PetTypeEnum petType;

    @NotBlank
    @Size(max = 120)
    private String location;

    @NotBlank
    @Size(max = 500)
    private String description;

}

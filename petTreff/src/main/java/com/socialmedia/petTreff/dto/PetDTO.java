package com.socialmedia.petTreff.dto;

import com.socialmedia.petTreff.entity.PetTypeEnum;

import lombok.Data;

@Data
public class PetDTO {

    private Long id; // petId
    private String name;
    private Long typeId;
    private Long ownerId;

    private PetTypeEnum type;

}

package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.PetTypeDTO;
import com.socialmedia.petTreff.entity.PetType;

public class PetTypeMapper {

    public static PetTypeDTO toDto(PetType petType) {
        if (petType == null) {
            return null;
        }
        PetTypeDTO dto = new PetTypeDTO();
        dto.setTypeName(petType.getTypeName());
        return dto;
    }

    public static PetType toEntity(PetTypeDTO petTypeDTO) {
        if (petTypeDTO == null) {
            return null;
        }
        PetType petType = new PetType();
        petType.setTypeName(petTypeDTO.getTypeName());
        return petType;
    }

}

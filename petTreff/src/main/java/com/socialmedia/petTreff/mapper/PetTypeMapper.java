package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.PetTypeDTO;
import com.socialmedia.petTreff.entity.PetType;

public class PetTypeMapper {

    public static PetTypeDTO toDto(PetType petType) {
        if (petType == null) {
            return null;
        }
        PetTypeDTO dto = new PetTypeDTO();
        String type = petType.getTypeName().name();
        dto.setTypeName(type);
        if (type == "DOG") {
            dto.setId(1L);
        }
        if (type == "CAT") {
            dto.setId(2L);
        }
        if (type == "BIRD") {
            dto.setId(3L);
        }
        if (type == "REPTILE") {
            dto.setId(4L);
        }
        if (type == "RABBIT") {
            dto.setId(5L);
        }
        if (type == "OTHER") {
            dto.setId(6L);
        }

        return dto;
    }

}

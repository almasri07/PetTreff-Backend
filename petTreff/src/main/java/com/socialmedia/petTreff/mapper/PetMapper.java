package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.PetDTO;
import com.socialmedia.petTreff.entity.Pet;
import com.socialmedia.petTreff.entity.PetType;
import com.socialmedia.petTreff.entity.User;

public class PetMapper {

    public static PetDTO toDto(Pet pet) {
        if (pet == null) {
            return null;
        }
        PetDTO petDTO = new PetDTO();
        petDTO.setName(pet.getName());
        petDTO.setId(pet.getId());

        if (pet.getType() != null) {
            petDTO.setTypeId(pet.getType().getId());
            petDTO.setType(pet.getType().getTypeName());
        }
        if (pet.getOwner() != null) {
            petDTO.setOwnerId(pet.getOwner().getId());
        }

        return petDTO;
    }

    public static Pet toEntity(PetDTO petDTO) {
        if (petDTO == null) {
            return null;
        }
        Pet pet = new Pet();
        pet.setName(petDTO.getName());
        pet.setId(petDTO.getId());

        if (petDTO.getTypeId() != null) {
            PetType type = new PetType();
            type.setId(petDTO.getTypeId());
            pet.setType(type);
        }

        if (petDTO.getOwnerId() != null) {
            User owner = new User();
            owner.setId(petDTO.getOwnerId());
            pet.setOwner(owner);
        }

        return pet;
    }

}

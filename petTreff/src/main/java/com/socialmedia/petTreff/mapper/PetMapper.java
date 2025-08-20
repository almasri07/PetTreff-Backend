package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.PetDTO;
import com.socialmedia.petTreff.entity.Pet;
import com.socialmedia.petTreff.repository.PetTypeRepository;

public class PetMapper {

    public static PetDTO toDto(Pet pet) {
        if (pet == null) {
            return null;
        }
        PetDTO petDTO = new PetDTO();
        petDTO.setName(pet.getName());
        petDTO.setTypeId(pet.getType().getId());
        petDTO.setId(pet.getId());

        return petDTO;
    }

    public static Pet toEntity(PetDTO petDTO, PetTypeRepository petTypeRepository) {
        if (petDTO == null) {
            return null;
        }
        Pet pet = new Pet();
        pet.setName(petDTO.getName());
        pet.setId(petDTO.getId());
        pet.setType(petTypeRepository.getReferenceById(petDTO.getTypeId()));
        return pet;
    }

}

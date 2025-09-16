package com.socialmedia.petTreff.dto;

import com.socialmedia.petTreff.entity.LookingFor;
import com.socialmedia.petTreff.entity.PetTypeEnum;

// com.socialmedia.petTreff.dto.ProfileUpdateDTO
public record ProfileUpdateDTO(
        String bio,
        String location,
        PetTypeEnum petType,
        LookingFor lookingFor,
        String topics,
        String days,

        String urlProfilePicture,
        Boolean allowMessages
) {}

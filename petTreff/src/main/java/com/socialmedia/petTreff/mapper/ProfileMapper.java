package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.ProfileUpdateDTO;
import com.socialmedia.petTreff.entity.Profile;

// com.socialmedia.petTreff.mapper.ProfileMapper
public class ProfileMapper {

    public static ProfileUpdateDTO toDto(Profile profile) {
        if (profile == null) return null;
        return new ProfileUpdateDTO(
                profile.getBio(),
                profile.getLocation(),
                profile.getPetType(),
                profile.getLookingFor(),
                profile.getTopics(),
                profile.getDays(),
                profile.getUrlProfilePicture(),
                profile.isAllowMessages()
        );
    }

    public static void updateEntityFromDto(ProfileUpdateDTO dto, Profile profile) {
        if (dto.bio() != null) profile.setBio(dto.bio());
        if (dto.location() != null) profile.setLocation(dto.location());
        if (dto.petType() != null) profile.setPetType(dto.petType());
        if (dto.lookingFor() != null) profile.setLookingFor(dto.lookingFor());
        if (dto.topics() != null) profile.setTopics(dto.topics());
        if (dto.days() != null) profile.setDays(dto.days());
        if (dto.allowMessages() != null) profile.setAllowMessages(dto.allowMessages());
        if(dto.urlProfilePicture() != null ) profile.setUrlProfilePicture(dto.urlProfilePicture());

    }
}

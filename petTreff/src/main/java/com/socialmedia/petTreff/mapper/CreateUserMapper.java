package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.CreateUserDTO;
import com.socialmedia.petTreff.entity.User;

public class CreateUserMapper {

    public static CreateUserDTO toDto(User user) {

        if (user == null) {
            return null;
        }
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername(user.getUsername());
        createUserDTO.setEmail(user.getEmail());
        createUserDTO.setPassword(user.getPassword()); // Assuming password is set in User entity
        createUserDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        return createUserDTO;

    }

    public static User toEntity(CreateUserDTO createUserDTO) {
        if (createUserDTO == null) {
            return null;
        }
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(createUserDTO.getPassword());
        user.setProfilePictureUrl(createUserDTO.getProfilePictureUrl());
        return user;
    }

}

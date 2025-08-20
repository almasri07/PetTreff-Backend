package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.entity.User;

public class UserMapper {

    public static UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDTO UserDTO = new UserDTO();
        UserDTO.setId(user.getId());
        UserDTO.setUsername(user.getUsername());
        UserDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        return UserDTO;
    }

    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setProfilePictureUrl(userDTO.getProfilePictureUrl());
        return user;
    }

}
package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.RoleDTO;
import com.socialmedia.petTreff.entity.Role;

public class RoleMapper {

    public static RoleDTO toDto(String role) {
        if (role == null) {
            return null;
        }

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setAuthority(role);
        return roleDTO;
    }

    public static Role toEntity(String roleDto) {
        if (roleDto == null) {
            return null;
        }

        return new Role(roleDto);
    }

}

package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.FriendshipDTO;
import com.socialmedia.petTreff.entity.Friendship;

public class FriendshipMapper {

    public static FriendshipDTO toDto(Friendship friendship) {
        if (friendship == null) {
            return null;
        }
        FriendshipDTO dto = new FriendshipDTO();
        dto.setId(friendship.getId());
        dto.setUserId(friendship.getUser().getId());
        dto.setFriendId(friendship.getFriend().getId());
        dto.setStatus(friendship.getStatus());
        return dto;
    }

    public static Friendship toEntity(FriendshipDTO dto) {
        if (dto == null) {
            return null;
        }
        Friendship friendship = new Friendship();
        friendship.setId(dto.getId());

        if (dto.getStatus() != null) {
            friendship.setStatus(dto.getStatus());
        }

        return friendship;
    }

}

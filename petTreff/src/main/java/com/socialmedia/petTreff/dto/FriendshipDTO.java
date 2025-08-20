package com.socialmedia.petTreff.dto;

import com.socialmedia.petTreff.entity.FriendshipStatus;
import lombok.Data;

@Data
public class FriendshipDTO {

    private Long id;
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;

}

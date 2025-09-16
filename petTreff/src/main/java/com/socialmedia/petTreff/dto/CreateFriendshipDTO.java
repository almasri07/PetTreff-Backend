package com.socialmedia.petTreff.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFriendshipDTO {


    @NotNull
    Long friendId;
}

package com.socialmedia.petTreff.dto;

import com.socialmedia.petTreff.entity.PetTypeEnum;

import lombok.Data;

import java.time.Instant;

@Data
public class MatchRequestDTO {
    private Long id;
    private PetTypeEnum petType;
    private String location;
    private String description;
    private Long authorId;
    private String authorUsername;
    private Instant createdAt;
}

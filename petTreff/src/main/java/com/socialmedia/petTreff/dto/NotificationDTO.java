package com.socialmedia.petTreff.dto;

import java.time.Instant;

import com.socialmedia.petTreff.entity.NotificationType;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String title;
    private String text;
    private Instant createdAt;
    private boolean read;
}

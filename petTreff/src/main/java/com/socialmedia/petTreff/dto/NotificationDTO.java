package com.socialmedia.petTreff.dto;

import com.socialmedia.petTreff.entity.NotificationType;
import lombok.Data;

import java.time.Instant;

@Data
public class NotificationDTO {
    private Long id;              // id von das Notification selber
    private NotificationType type;
    private String title;
    private String text;
    private Instant createdAt;
    private boolean read;
    private Long actorId;
    private Long refId;    // id von aufgerufene Benachrichtigung aus seine eigner Tabelle
}

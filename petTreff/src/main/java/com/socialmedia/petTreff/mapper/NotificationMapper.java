package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.NotificationDTO;
import com.socialmedia.petTreff.entity.Notification;

public class NotificationMapper {
    public static NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setText(n.getText());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setRead(n.getReadAt() != null);
        return dto;
    }
}
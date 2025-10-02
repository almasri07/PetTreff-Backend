package com.socialmedia.petTreff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String senderUsername;
    private String recipientUsername;
    private String content;
    private LocalDateTime dateTime;
    private Long chatRoomId;
}

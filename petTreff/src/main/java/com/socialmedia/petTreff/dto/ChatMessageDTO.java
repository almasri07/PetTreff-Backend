package com.socialmedia.petTreff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private String senderUsername;
    private String recipientUsername;
    private String content;
    private LocalDateTime dateTime;
}

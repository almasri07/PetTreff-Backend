package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.ChatMessageDTO;
import com.socialmedia.petTreff.entity.ChatMessage;
import com.socialmedia.petTreff.entity.User;

public class ChatMessageMapper {


    public static ChatMessageDTO toDTO(ChatMessage entity, User sender, User recipient) {
        if (entity == null || sender == null || recipient == null) {
            return null;
        }
        return new ChatMessageDTO(
                entity.getId(),
                sender.getUsername(),
                recipient.getUsername(),
                entity.getContent(),
                entity.getDateTime()
        );
    }

    public static ChatMessage toEntity(ChatMessageDTO dto, Long senderId, Long recipientId, Long chatRoomId) {
        if (dto == null) {
            return null;
        }
        ChatMessage entity = new ChatMessage();
        entity.setId(dto.getId());
        entity.setSenderId(senderId);
        entity.setRecipientId(recipientId);
        entity.setChatRoomId(chatRoomId);
        entity.setContent(dto.getContent());
        entity.setDateTime(dto.getDateTime());
        return entity;
    }
}

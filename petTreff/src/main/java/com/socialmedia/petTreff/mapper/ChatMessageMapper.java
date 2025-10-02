package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.ChatMessageDTO;
import com.socialmedia.petTreff.entity.ChatMessage;
import com.socialmedia.petTreff.repository.UserRepository;

public class ChatMessageMapper {




    public static ChatMessageDTO toDTO(ChatMessage entity, UserRepository userRepository) {
        if (entity == null)
            return null;
        var sender = userRepository.findById(entity.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found!"));
        var recipient = userRepository.findById(entity.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found!"));

        return new ChatMessageDTO(
                entity.getId(),
                sender.getId(),
                recipient.getId(),
                sender.getUsername(),
                recipient.getUsername(),
                entity.getContent(),
                entity.getDateTime(),
                entity.getChatRoomId()
        );
    }

    public static ChatMessage toEntity(ChatMessageDTO dto, UserRepository userRepository) {
        if (dto == null) return null;

        Long senderId = dto.getSenderId();
        Long recipientId = dto.getRecipientId();

        if (senderId == null) {
            senderId = userRepository.findByUsername(dto.getSenderUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found!"))
                    .getId();
        }
        if (recipientId == null) {
            recipientId = userRepository.findByUsername(dto.getRecipientUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found!"))
                    .getId();
        }

        ChatMessage entity = new ChatMessage();
        entity.setId(dto.getId());
        entity.setSenderId(senderId);
        entity.setRecipientId(recipientId);
        entity.setChatRoomId(dto.getChatRoomId());
        entity.setContent(dto.getContent());
        entity.setDateTime(dto.getDateTime());
        return entity;
    }

}

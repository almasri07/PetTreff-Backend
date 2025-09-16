package com.socialmedia.petTreff.service;


import com.socialmedia.petTreff.entity.ChatMessage;
import com.socialmedia.petTreff.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {

        Long roomId = chatRoomService.getOrCreateChatRoomId( chatMessage.getSenderId(),
                chatMessage.getRecipientId());

        chatMessage.setChatRoomId(roomId);

        if (chatMessage.getDateTime() == null) {
            chatMessage.setDateTime(LocalDateTime.now());
        }
        ChatMessage saved = chatMessageRepository.save(chatMessage);

        return saved;

    }

    @Transactional(readOnly = true)
   public List<ChatMessage> getChatMessages(Long userXid, Long userYid) {

        long u1 = Math.min(userXid, userYid);
        long u2 = Math.max(userXid, userYid);

        return chatRoomService.findRoomId(u1, u2)
                .map(chatMessageRepository::findByChatRoomIdOrderByDateTimeAsc)
                .orElseGet(List::of);
   }


}

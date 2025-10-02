package com.socialmedia.petTreff.service;


import com.socialmedia.petTreff.dto.ChatMessageDTO;
import com.socialmedia.petTreff.entity.ChatMessage;
import com.socialmedia.petTreff.mapper.ChatMessageMapper;
import com.socialmedia.petTreff.repository.ChatMessageRepository;
import com.socialmedia.petTreff.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Transactional
    public ChatMessageDTO save(ChatMessage chatMessage) {

        var sender = userRepository.findById(chatMessage.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found!"));
        var recipient = userRepository.findById(chatMessage.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found!"));


        Long roomId = chatRoomService.getOrCreateChatRoomId(sender.getId(),
                recipient.getId());

        chatMessage.setChatRoomId(roomId);

        if (chatMessage.getDateTime() == null) {
            chatMessage.setDateTime(LocalDateTime.now());
        }
        ChatMessage saved = chatMessageRepository.save(chatMessage);

        return ChatMessageMapper.toDTO(saved,userRepository);

    }

    @Transactional(readOnly = true)
   public List<ChatMessageDTO> getChatMessages(Long userXid, Long userYid) {

        long u1 = Math.min(userXid, userYid);
        long u2 = Math.max(userXid, userYid);

        return chatRoomService.findRoomId(u1, u2)
                .map(chatMessageRepository::findByChatRoomIdOrderByDateTimeAsc)
                .map(list -> list.stream()
                        .map(m -> ChatMessageMapper.toDTO(m, userRepository))
                        .toList())
                .orElseGet(List::of);
   }


}

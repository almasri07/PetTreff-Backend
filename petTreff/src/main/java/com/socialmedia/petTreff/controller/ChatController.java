package com.socialmedia.petTreff.controller;


import com.socialmedia.petTreff.dto.ChatMessageDTO;
import com.socialmedia.petTreff.entity.ChatMessage;
import com.socialmedia.petTreff.entity.NotificationType;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.ChatMessageMapper;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.ChatMessageService;
import com.socialmedia.petTreff.service.ChatRoomService;
import com.socialmedia.petTreff.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final ChatRoomService chatRoomService;

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(@PathVariable("senderId") Long senderId, @PathVariable("recipientId") Long recipientId)
    {

        long u1 = Math.min(senderId, recipientId);
        long u2 = Math.max(senderId, recipientId);

        List<ChatMessage> messages = chatMessageService.getChatMessages(u1, u2);

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Recipient not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        return ResponseEntity.ok(messages.stream()
                .map( m -> ChatMessageMapper.toDTO(m, sender, recipient)).toList());
    }

    @MessageMapping("/chat")
    public void sendMessage(@AuthenticationPrincipal UserPrincipal me , @Payload ChatMessage chatMessage) {

        if (!me.getId().equals(chatMessage.getSenderId())) {
            throw new IllegalArgumentException("Sender mismatch");
        }

      Long roomId = chatRoomService.getOrCreateChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId());
      chatMessage.setChatRoomId(roomId);

      if(chatMessage.getDateTime() == null){  chatMessage.setDateTime(LocalDateTime.now()); }

      ChatMessage savedMessage = chatMessageService.save(chatMessage);


      User recipient = userRepository.findById(savedMessage.getRecipientId())
              .orElseThrow(() -> new EntityNotFoundException("Recipient not found"));

      User sender = userRepository.findById(savedMessage.getSenderId())
              .orElseThrow(() -> new EntityNotFoundException("Sender not found"));


      // speichert notification for Empfänger
      notificationService.create(
              savedMessage.getRecipientId(),
              recipient.getUsername(),
              NotificationType.CHAT,
              "New message from " + sender.getUsername(),
              savedMessage.getContent(),
              savedMessage.getSenderId(),
              savedMessage.getId()
      );

      ChatMessageDTO chatMsgDTO = ChatMessageMapper.toDTO(savedMessage, sender, recipient);



      // zeigt chat bubble also UI wurde geupdated

      simpMessagingTemplate.convertAndSendToUser(
              recipient.getUsername()
              , "/queue/messages",
               chatMsgDTO
      );

    }



}

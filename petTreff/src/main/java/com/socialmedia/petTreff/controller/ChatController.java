package com.socialmedia.petTreff.controller;


import com.socialmedia.petTreff.dto.ChatMessageDTO;
import com.socialmedia.petTreff.dto.ChatSendDTO;
import com.socialmedia.petTreff.entity.ChatMessage;
import com.socialmedia.petTreff.entity.NotificationType;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.service.ChatMessageService;
import com.socialmedia.petTreff.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController // works for REST + can host @MessageMapping methods
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(value = "${frontend.url}", allowCredentials = "true")
public class ChatController {

    private final ChatMessageService chatService;
    private final SimpMessagingTemplate messaging;
    private final UserRepository userRepository;

    private final NotificationService notificationService;

    // ---- REST: history ------------------------------------------------------
    @GetMapping("/{senderId}/{recipientId}")
    public List<ChatMessageDTO> history(@PathVariable Long senderId, @PathVariable Long recipientId) {
        return chatService.getChatMessages(senderId, recipientId);
    }

    // ---- WebSocket: send message (/app/chat) --------------------------------
    @MessageMapping("/chat")
    public void handleChat(@Payload ChatSendDTO in, Principal principal) {

        if (principal == null ) throw new IllegalArgumentException("No Principal");

       // log.info("WS /chat from principal={}", senderKey);

        var sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found!"));


        var recipient = userRepository.findById(in.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found!"));

        var senderUsername = sender.getUsername();

        var recipientUsername = recipient.getUsername();


        var messageToSave = new ChatMessage();
        messageToSave.setContent(in.getContent());
        messageToSave.setSenderId(sender.getId());
        messageToSave.setRecipientId(recipient.getId());

        ChatMessageDTO saved = chatService.save(messageToSave);

        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.CHAT_MESSAGE,
                "Someone sent you a message",
                sender.getUsername() + " has sent you a message", sender.getId(), saved.getId());


        log.info("WS OUT dto: senderId={}, recipientId={}, senderUsername={}, recipientUsername={}",
                saved.getSenderId(), saved.getRecipientId(), saved.getSenderUsername(), saved.getRecipientUsername());


        // echo to sender and deliver to recipient via user queues
        messaging.convertAndSendToUser(senderUsername,   "/queue/messages", saved);
        messaging.convertAndSendToUser(recipientUsername, "/queue/messages", saved);
    }
}

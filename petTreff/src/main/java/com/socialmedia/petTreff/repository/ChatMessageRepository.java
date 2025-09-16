package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderByDateTimeAsc(Long chatRoomId);

}

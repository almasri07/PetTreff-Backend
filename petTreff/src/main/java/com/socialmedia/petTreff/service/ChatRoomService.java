package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.entity.ChatRoom;
import com.socialmedia.petTreff.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public Long getOrCreateChatRoomId(Long userIdX, Long userIdY) {
        long id1 = Math.min(userIdX, userIdY);
        long id2 = Math.max(userIdX, userIdY);

        return chatRoomRepository
                .findByUser1IdAndUser2Id(id1, id2)
                .map(ChatRoom::getId)
                .orElseGet(() -> {

                    ChatRoom cr = new ChatRoom();
                    cr.setUser1Id(id1);
                    cr.setUser2Id(id2);
                    return chatRoomRepository.save(cr).getId();
                });

    }

    public Optional<Long> findRoomId(Long a, Long b) {
        long u1 = Math.min(a, b);
        long u2 = Math.max(a, b);
        return chatRoomRepository.findByUser1IdAndUser2Id(u1, u2).map(ChatRoom::getId);
    }


}

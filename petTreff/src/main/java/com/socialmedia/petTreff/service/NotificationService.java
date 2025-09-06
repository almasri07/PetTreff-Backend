package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.NotificationDTO;
import com.socialmedia.petTreff.entity.Notification;
import com.socialmedia.petTreff.entity.NotificationType;
import com.socialmedia.petTreff.mapper.NotificationMapper;
import com.socialmedia.petTreff.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;

    @Transactional(readOnly = true)
    public List<NotificationDTO> recentFor(Long userId) {
        return repo.findTop10ByRecipientIdOrderByCreatedAtDesc(userId)
                .stream().map(NotificationMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {

        return repo.countByRecipientIdAndReadAtIsNull(userId);
    }


    @Transactional
    public boolean markOneRead(Long notifId, Long userId) {
        // markOneAsRead Methode gibt 1 falls erfolgreich gelesen wurde und 0 falls nichts
        return repo.markOneAsRead(notifId, userId) > 0;
    }

    @Transactional
    public void markAllRead(Long userId) {
        repo.markAllAsRead(userId);
    }


    // hilft bei der Erstellung von Benachrichtigungen
    @Transactional
    public NotificationDTO create(Long recipientId, String recipientName, NotificationType type, String title,
                                  String text, Long actorId, Long refId) {

        Notification n = new Notification();
        n.setRecipientId(recipientId);
        n.setRecipientName(recipientName);
        n.setType(type);
        n.setTitle(title);
        n.setText(text);
        n.setActorId(actorId);
        n.setRefId(refId);

        Notification saved = repo.save(n);
        return NotificationMapper.toDTO(saved);
    }
}

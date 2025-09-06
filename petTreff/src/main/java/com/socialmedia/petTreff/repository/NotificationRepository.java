package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop10ByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndReadAtIsNull(Long recipientId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id AND n.recipientId = :uid AND n.readAt IS NULL")
    int markOneAsRead(Long id, Long uid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.readAt = CURRENT_TIMESTAMP WHERE n.recipientId = :uid AND n.readAt IS null")
    int markAllAsRead(Long uid);

}

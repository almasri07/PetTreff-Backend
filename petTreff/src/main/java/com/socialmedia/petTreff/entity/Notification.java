package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table( name = "notification",
        indexes = {
                @Index(name = "idx_notif_rec_created", columnList = "recipientId,createdAt"),
                @Index(name = "idx_notif_rec_read",    columnList = "recipientId,readAt")})
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipientId; // wer bekommt die Benachrichtigung
    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String title; // "Friend Request", "New Message"...
    @Column(nullable = false)
    private String text; // der eigentliche Text der Benachrichtigung
    @CreationTimestamp   // weist createdAt zu Zeitpunkt der Erstellung vor der Speicherung in der DB
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Instant readAt; // null = ungelesen, sonst Zeitstempel wann gelesen
    private Long actorId; // wer hat die Aktion ausgelöst (z.B. Freundschaftsanfrage gesendet)
    private Long refId; // Referenz-ID für die Aktion (z.B. Freundschaftsanfrage ID, Chat ID, etc.)


}

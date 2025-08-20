package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "match_interest", uniqueConstraints = @UniqueConstraint(columnNames = { "match_request_id",
        "sender_id", "status" }))
@Getter
@Setter
@NoArgsConstructor
public class MatchInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_request_id", nullable = false)
    private Long matchRequestId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_username", nullable = false, length = 60)
    private String senderUsername;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestStatus status = InterestStatus.PENDING;


}

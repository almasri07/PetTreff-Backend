package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "match_requests", uniqueConstraints = { @UniqueConstraint(columnNames = { "author_id", "status" }) })
@Getter
@Setter
@NoArgsConstructor
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PetTypeEnum petType;


    @Column(nullable = false, length = 120)
    private String location;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 60)
    private String authorUsername;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchRequestStatus status = MatchRequestStatus.OPEN;





}

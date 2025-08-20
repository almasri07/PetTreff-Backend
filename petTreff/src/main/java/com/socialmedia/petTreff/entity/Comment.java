package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/*
Warum kein @Data bei JPA-Entities?
- @Data macht equals/hashCode & toString über alle Felder → Rekursion, Lazy-Loads, unnötige DB-Hits.
- Hibernate-Proxies + feldbasierte equals/hashCode → kaputte Identität.
*/

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 700)
    private String content;

    @Version // um Race Conditions zu vermeiden
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // optional und fetsch heir lazy, um die Performance zu verbessern
    // sodas wird die User- und Post-Entität erst geladen, wenn
    // sie wirklich benötigt wird
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

}
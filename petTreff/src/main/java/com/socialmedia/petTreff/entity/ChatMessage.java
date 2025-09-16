package com.socialmedia.petTreff.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chatRoomId;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Size(max=2000)
    @Column(length = 2000, nullable = false)
    private String content;

}

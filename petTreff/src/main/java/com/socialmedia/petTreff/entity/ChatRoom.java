package com.socialmedia.petTreff.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "chat_room",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
)
public class ChatRoom {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "user1_id", nullable = false )
 @EqualsAndHashCode.Include
 private Long user1Id;

 @Column(name = "user2_id", nullable = false )
 @EqualsAndHashCode.Include
 private Long user2Id;

}

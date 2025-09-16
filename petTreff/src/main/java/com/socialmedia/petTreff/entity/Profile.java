package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "profiles",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
@Getter @Setter
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true) // <-- eindeutig
    private User user;

    @Column(length = 500)
    private String bio;

    private String location;

    @Enumerated(EnumType.STRING)
    private PetTypeEnum petType;

    @Enumerated(EnumType.STRING)
    private LookingFor lookingFor;  // SPIELTREFFEN/TRAINING/...

    private String topics;          //
    private String days;

    private boolean allowMessages;

    private String urlProfilePicture;
}

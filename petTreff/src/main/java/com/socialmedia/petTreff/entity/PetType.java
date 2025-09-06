package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "pet_type", uniqueConstraints = @UniqueConstraint(name = "uk_pettype_name", columnNames = "type_name"))

public class PetType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_name", nullable = false, unique = true, length = 40)
    private PetTypeEnum typeName;
}
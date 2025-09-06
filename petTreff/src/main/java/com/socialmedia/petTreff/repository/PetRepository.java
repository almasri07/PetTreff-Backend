package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByIdAndOwner_Id(Long id, Long ownerId);
    boolean existsByIdAndOwner_Id(Long id, Long ownerId);
}

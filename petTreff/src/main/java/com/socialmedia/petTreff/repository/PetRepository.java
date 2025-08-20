package com.socialmedia.petTreff.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialmedia.petTreff.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

}

package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.PetType;
import com.socialmedia.petTreff.entity.PetTypeEnum;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetTypeRepository extends JpaRepository<PetType, Long> {

    Optional<PetType> findByTypeName(PetTypeEnum typeName);
}

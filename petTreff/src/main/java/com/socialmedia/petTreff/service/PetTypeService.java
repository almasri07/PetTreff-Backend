package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.entity.PetType;
import com.socialmedia.petTreff.repository.PetTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PetTypeService {

    @Autowired
    private PetTypeRepository petTypeRepository;

    @Transactional(readOnly = true)
    public List<PetType> getAllPetTypes() {
        return petTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PetType> getPetTypeById(Long id) {
        return petTypeRepository.findById(id);
    }

    @Transactional
    public PetType createPetType(PetType petType) {
        return petTypeRepository.save(petType);
    }

    @Transactional
    public PetType updatePetType(Long id, PetType petType) {
        petType.setId(id);
        return petTypeRepository.save(petType);
    }

    @Transactional
    public void deletePetType(Long id) {
        petTypeRepository.deleteById(id);
    }
}

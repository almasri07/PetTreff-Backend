package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.PetTypeDTO;
import com.socialmedia.petTreff.mapper.PetTypeMapper;
import com.socialmedia.petTreff.repository.PetTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pet-types")
@RequiredArgsConstructor
public class PetTypeController {

    private final PetTypeRepository petTypeRepository;

    @GetMapping
    public List<PetTypeDTO> getAllPetTypes() {
        return petTypeRepository.findAll()
                .stream()
                .map(PetTypeMapper::toDto)
                .toList();
    }

}
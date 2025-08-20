package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.PetDTO;
import com.socialmedia.petTreff.entity.Pet;
import com.socialmedia.petTreff.entity.PetType;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.PetMapper;
import com.socialmedia.petTreff.repository.PetRepository;
import com.socialmedia.petTreff.repository.PetTypeRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    private final UserRepository userRepository;

    private final PetTypeRepository petTypeRepository;

    @Transactional(readOnly = true)
    public List<PetDTO> getAllPets() {

        return petRepository.findAll().stream()
                .map(PetMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PetDTO getPetById(Long id) {

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found with id: " + id));

        return PetMapper.toDto(pet);

    }

    @Transactional
    public PetDTO createPet(PetDTO petReq, Long userId) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to create pet for another user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Pet newPet = PetMapper.toEntity(petReq, petTypeRepository);
        newPet.setOwner(user);
        petRepository.save(newPet);
        return PetMapper.toDto(newPet);
    }

    @Transactional
    public PetDTO updatePet(Long id, PetDTO req) {

        Pet toUpdatePet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found with id: " + id));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!toUpdatePet.getOwner().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to update this pet");
        }

        if (req.getName() != null) {
            toUpdatePet.setName(req.getName());
        }
        if (req.getTypeId() != null) {
            PetType petType = petTypeRepository.findById(req.getTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("PetType not found with id: " + req.getTypeId()));
            toUpdatePet.setType(petType);

        }

        return PetMapper.toDto(petRepository.save(toUpdatePet));
    }

    @Transactional
    public void deletePet(Long id) {

        Pet toDeletePet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found with id: " + id));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!toDeletePet.getOwner().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to delete this pet");
        }

        petRepository.deleteById(id);
    }
}

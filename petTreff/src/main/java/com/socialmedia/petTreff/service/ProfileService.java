package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.ProfileUpdateDTO;
import com.socialmedia.petTreff.entity.Profile;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.repository.ProfileRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// com.socialmedia.petTreff.service.ProfileService
@Service
@RequiredArgsConstructor
public class ProfileService {

    private  final UserRepository userRepository;
    private  final ProfileRepository profileRepository;


    @Transactional
    public Profile updateMyProfile(UserPrincipal principal, ProfileUpdateDTO dto) {


        Profile profile = profileRepository.findByUser_Id(principal.getId()).orElseGet(() -> {
            User user = userRepository.findById(principal.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + principal.getId()));
            Profile p = new Profile();
            p.setUser(user);
            return p;
        });

        profile.setLocation(dto.location());
        profile.setBio(dto.bio());
        profile.setPetType(dto.petType());
        profile.setLookingFor(dto.lookingFor());
        profile.setTopics(dto.topics());
        profile.setDays(dto.days());
        profile.setUrlProfilePicture(dto.urlProfilePicture());
        profile.setAllowMessages(Boolean.TRUE.equals(dto.allowMessages()));


        return profileRepository.save(profile);
    }

}

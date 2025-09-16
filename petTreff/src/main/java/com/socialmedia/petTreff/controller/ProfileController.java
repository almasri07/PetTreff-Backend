package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.ProfileUpdateDTO;
import com.socialmedia.petTreff.mapper.ProfileMapper;
import com.socialmedia.petTreff.repository.ProfileRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// com.socialmedia.petTreff.controller.ProfileController
@RestController
@RequestMapping("/api/profile")
@CrossOrigin(value = "${frontend.url}", allowCredentials = "true")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    @PutMapping()
    public ResponseEntity<ProfileUpdateDTO> updateMe(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ProfileUpdateDTO body
    ) {
        var updated = profileService.updateMyProfile(principal, body);

        return ResponseEntity.ok(ProfileMapper.toDto(updated));
    }



    @GetMapping()
    public ResponseEntity<ProfileUpdateDTO> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        var u = profileRepository.findByUser_Id(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return ResponseEntity.ok(ProfileMapper.toDto(u));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileUpdateDTO> getByUserId(@PathVariable Long userId) {
        return profileRepository.findByUser_Id(userId)
                .map(p -> ResponseEntity.ok(ProfileMapper.toDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}

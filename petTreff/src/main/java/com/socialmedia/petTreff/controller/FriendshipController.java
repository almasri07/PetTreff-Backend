package com.socialmedia.petTreff.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.socialmedia.petTreff.dto.FriendshipDTO;
import com.socialmedia.petTreff.entity.Friendship;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.FriendshipService;

@RestController
@RequestMapping("/api/friendships")
@Validated
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping
    public ResponseEntity<List<Friendship>> getAllFriendships() {
        return ResponseEntity.ok(friendshipService.getAllFriendships());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendshipDTO> getFriendshipById(@PathVariable Long id) {

        return friendshipService.getFriendshipById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Friendship not found"));
    }

    @PostMapping
    public ResponseEntity<FriendshipDTO> createFriendship(@RequestBody FriendshipDTO friendshipDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        FriendshipDTO savedFriendship = friendshipService.createFriendship(friendshipDTO, userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedFriendship);

    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<FriendshipDTO> accept(@PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return ResponseEntity.ok(friendshipService.accept(id, userPrincipal.getId()));

    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<FriendshipDTO> decline(@PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return ResponseEntity.ok(friendshipService.decline(id, userPrincipal.getId()));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long id) {

        friendshipService.deleteFriendship(id);

        return ResponseEntity.noContent().build();
    }

}

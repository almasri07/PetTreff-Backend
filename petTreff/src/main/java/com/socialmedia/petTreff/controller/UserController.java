package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.PasswordChangeDTO;
import com.socialmedia.petTreff.dto.PetDTO;
import com.socialmedia.petTreff.dto.PostDTO;
import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<PasswordChangeDTO> updateUserPassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {

        userService.changePassword(id, passwordChangeDTO.getNewPassword());
        return ResponseEntity.ok(passwordChangeDTO);
    }

    @PutMapping("/{id}/profile-picture")
    public ResponseEntity<UserDTO> updateProfilePicture(
            @PathVariable Long id,
            @RequestBody String profilePictureUrl) {
        UserDTO updatedUser = userService.updateProfilePicture(id, profilePictureUrl);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<UserDTO> updateUsername(@PathVariable Long id, @RequestBody String newUsername) {
        return ResponseEntity.ok(userService.updateUsername(id, newUsername));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserDTO> updateEmail(@PathVariable Long id, @RequestBody String newEmail) {
        return ResponseEntity.ok(userService.updateEmail(id, newEmail));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {

        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/{id}/pets")
    public ResponseEntity<List<PetDTO>> getUserPets(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserPets(id));
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserPosts(id));
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<UserDTO>> getUserFriends(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserFriends(id));
    }
    @GetMapping("/{id}/mutual-friends/{otherUserId}")
    public ResponseEntity<Set<UserDTO>> getMutualUserFriends(@PathVariable Long id ,
                                                             @PathVariable Long otherUserId )
    {

        return ResponseEntity.ok(userService.getMutualUserFriends(id , otherUserId));
    }

    @GetMapping("/{id}/getFriendsCount")
    public ResponseEntity<Integer> getFriendsCount(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFriendsCount(id));
    }


}

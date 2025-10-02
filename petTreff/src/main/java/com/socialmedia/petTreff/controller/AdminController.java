package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.UserMapper;
import com.socialmedia.petTreff.service.MatchService;
import com.socialmedia.petTreff.service.PostService;
import com.socialmedia.petTreff.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Optional: adds role check at class level
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final UserService userService;
    private final MatchService matchService;

    private final PostService postService;
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Admin!");
    }


    // NUR FÜR ADMIN IMPLEMENTIERERN    CHECKED
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(defaultValue = "10") int limit) {

        List<User> users = userService.getAllUsers(Math.max(1, Math.min(limit, 25))).stream().map(UserMapper::toEntity).toList();
        return ResponseEntity.ok(users);
    }

    // NUR FÜR ADMIN                   CHECKED
    @DeleteMapping("/{id}/deleteUser")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    // FÜR BEIDE ADMIN UND USER implementieren                  CHECKED
    @DeleteMapping("/{id}/deleteMatchRequest")
    public ResponseEntity<Void> deleteMatchRequest(@PathVariable Long id) {
        matchService.adminDeleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    // FÜR ADMIN implementieren                     CHECKED
    @DeleteMapping("/{id}/deletePost")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.adminDeletePost(id);
        return ResponseEntity.noContent().build();
    }



}

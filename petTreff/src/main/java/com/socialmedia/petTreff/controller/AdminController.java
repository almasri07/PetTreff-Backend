package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.UserMapper;
import com.socialmedia.petTreff.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Optional: adds role check at class level
@Validated
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Admin!");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userService.getAllUsers().stream().map(UserMapper::toEntity).toList();
        return ResponseEntity.ok(users);
    }

    // zusätzliche Admin-Funktionen können hier hinzugefügt werden
}

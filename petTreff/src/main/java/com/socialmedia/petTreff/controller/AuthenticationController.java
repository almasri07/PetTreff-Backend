package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.CreateUserDTO;
import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.UserMapper;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// die Klasse ist für Login, Regesteration ...
@RestController
@RequestMapping("/auth")
@CrossOrigin(value = "${frontend.url}", allowCredentials = "true")
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/me")
    public User getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserDTO userDTO = userService.getUserById(principal.getId()).orElse(null);


        return UserMapper.toEntity(userDTO);
    }


    // Komplett Pfad http://localhost:8080/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody CreateUserDTO user) {

        User newUser = userService.createUser(user);

        System.out.println("user is "+ user);


        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,@RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

}

package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.CreateUserDTO;
import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.UserMapper;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

// die Klasse ist für Regesteration .
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(value = "${frontend.url}", allowCredentials = "true")
public class AuthenticationController {

    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping("/me")
    public User getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserDTO userDTO = userService.getUserById(principal.getId()).orElse(null);

        return UserMapper.toEntity(userDTO);
    }

    @GetMapping("/roles")
    public Set<String> getRoles(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return Set.of(); // or throw 401
        }

        return principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // "ROLE_USER", "ROLE_ADMIN"
                .collect(Collectors.toSet());
    }



    // Komplett Pfad http://localhost:8080/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody CreateUserDTO user) {

        User newUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,@RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

/*
    // CSRF endpoint
    @GetMapping("/csrf-token")
    public CsrfToken csrf(CsrfToken token) {
        // Spring will inject the current CSRF token,
        // and also send the cookie to the browser.
        return token;
    }
*/

}

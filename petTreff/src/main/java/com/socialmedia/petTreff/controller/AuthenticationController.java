package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.CreateUserDTO;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Komplett Pfad http://localhost:8080/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody CreateUserDTO user) {

        User newUser = userService.createUser(user);


        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

/*       NUR FALLS TOKENS GENUTZT WERDEN DANN WIRD SOLCHE METHODE BENÖTIGT

      @PostMapping("/login")
      public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        return ResponseEntity.ok(new LoginResponse(token));

    }


 */
    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,@RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

}

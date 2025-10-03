package com.socialmedia.petTreff.controller;



import com.socialmedia.petTreff.service.PasswordResetOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordResetOtpController {
    private final PasswordResetOtpService prService;

    @PostMapping(value = "/forgot-password", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> forgot(@RequestBody String email) {
        prService.start(email.trim());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<Void> verify(@RequestBody Map<String,String> body) {
        prService.verifyAndReset(body.get("email"), body.get("code"), body.get("newPassword"));
        return ResponseEntity.noContent().build();
    }
}
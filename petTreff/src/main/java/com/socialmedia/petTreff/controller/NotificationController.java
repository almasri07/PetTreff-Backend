package com.socialmedia.petTreff.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.socialmedia.petTreff.dto.NotificationDTO;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping
    public List<NotificationDTO> list(@AuthenticationPrincipal UserPrincipal user) {
        return service.recentFor(user.getId());
    }

    @GetMapping("/count")
    public Map<String, Long> count(@AuthenticationPrincipal UserPrincipal user) {
        return Map.of("unread", service.unreadCount(user.getId()));
    }

    @PostMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readAll(@AuthenticationPrincipal UserPrincipal user) {
        service.markAllRead(user.getId());
    }
}

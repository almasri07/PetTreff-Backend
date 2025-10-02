package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.NotificationDTO;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping
    public List<NotificationDTO> list(@AuthenticationPrincipal UserPrincipal user) {
        return service.recentFor(user.getId());
    }


    @GetMapping("/oneNotfication/{id}")
    public NotificationDTO getOneNotification(@AuthenticationPrincipal UserPrincipal user,
                                              @PathVariable("id") Long refId) {
        return service.getOneNotification(user.getId(), refId);
    }





    @GetMapping("/count")
    public Map<String, Long> count(@AuthenticationPrincipal UserPrincipal user) {
        return Map.of("unread", service.unreadCount(user.getId()));
    }

    @PostMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readOne(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        service.markOneRead(id, user.getId());
    }

    @PostMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readAll(@AuthenticationPrincipal UserPrincipal user) {

        service.markAllRead(user.getId());
    }
}

package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.CreateMatchRequestDTO;
import com.socialmedia.petTreff.dto.MatchRequestDTO;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchRequestDTO> createMatch(@RequestBody CreateMatchRequestDTO req,
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createMatchRequest(req, user));

    }

    @GetMapping("/recent")
    public List<MatchRequestDTO> recent(@RequestParam(defaultValue = "10") int limit) {

        return matchService.getRecentRequests(Math.max(1, Math.min(limit, 25)));

    }

    @PostMapping("/{id}/send-interest")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendInterest(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {

        matchService.sendInterest(id, user);

    }

    @PostMapping("/interests/{interestId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(@PathVariable Long interestId, @AuthenticationPrincipal UserPrincipal user) {
        matchService.accept(interestId, user);
    }

    @PostMapping("/interests/{interestId}/decline")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decline(@PathVariable Long interestId, @AuthenticationPrincipal UserPrincipal user) {
        matchService.decline(interestId, user);
    }

    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void close(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        matchService.closeRequest(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatchRequest(@PathVariable Long id) {
        matchService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/unsent-interest")
    public ResponseEntity<Void> deleteInterest(@PathVariable Long id) {
        matchService.deleteInterest(id);
        return ResponseEntity.noContent().build();
    }


}

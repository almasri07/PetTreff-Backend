package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.CreateMatchRequestDTO;
import com.socialmedia.petTreff.dto.MatchRequestDTO;
import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
    public ResponseEntity<Void> deleteMatchRequest(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        matchService.deleteRequest(id, principal);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/unsent-interest")
    public ResponseEntity<Void> deleteInterest(@PathVariable Long id) {
        matchService.deleteInterest(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{matchId}/accepted-peer")
    public ResponseEntity<UserDTO> getAcceptedPeer(
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return matchService.findAcceptedPeer(matchId, principal.getId());
    }



    @GetMapping("/currentMatchId")
    public ResponseEntity<Long> getCurrentMatchId(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        return matchService.getCurrentMatchIdForUser(principal.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build()); // 204 avoids red 404 in devtools
    }






}

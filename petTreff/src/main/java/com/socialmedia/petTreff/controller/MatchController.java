package com.socialmedia.petTreff.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.socialmedia.petTreff.dto.CreateMatchRequestDTO;
import com.socialmedia.petTreff.dto.MatchRequestDTO;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.MatchService;

import lombok.RequiredArgsConstructor;

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

    @PostMapping("/{id}/send-request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendRequest(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {

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

}

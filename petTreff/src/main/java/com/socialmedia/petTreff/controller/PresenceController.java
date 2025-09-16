package com.socialmedia.petTreff.controller;


import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.PresenceService;
import com.socialmedia.petTreff.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presence")
public class PresenceController {

    private final PresenceService presenceService;
    private final UserService userService;



    @GetMapping("/friends")
    public Set<UserDTO> getOnlineFriends(@AuthenticationPrincipal UserPrincipal userPrincipal){


        Set<UserDTO> friendsDto = userService.getUserFriends(userPrincipal.getId());

        return friendsDto.stream()
                .filter( friend -> presenceService.isOnline(friend.getId()))
                .collect(Collectors.toSet());

    }

}

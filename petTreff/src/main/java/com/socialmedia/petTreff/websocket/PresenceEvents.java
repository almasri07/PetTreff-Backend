package com.socialmedia.petTreff.websocket;

import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class PresenceEvents {

    private final PresenceService presenceService;


    // SessionConnectEvent : Spring-Event, das ausgelöst wird,
    // wenn ein Client eine STOMP/WebSocket-Verbindung aufbauen will
    @EventListener
    public void handleConnect(SessionConnectEvent event) {

            if( event.getUser() instanceof UserPrincipal userPrincipal) {
                presenceService.markAsOnline(userPrincipal.getId());
            }

    }

    @EventListener
    public void handleDisConnect(SessionDisconnectEvent event) {

        if( event.getUser() instanceof UserPrincipal userPrincipal) {
            presenceService.markAsOffline(userPrincipal.getId());
        }
    }



}

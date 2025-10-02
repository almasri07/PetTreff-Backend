package com.socialmedia.petTreff.websocket;

import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceEvents {

    private final PresenceService presenceService;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = (String) event.getMessage().getHeaders()
                .get(SimpMessageHeaderAccessor.SESSION_ID_HEADER);
        Object user = sha.getUser(); // or event.getUser()

        if (user instanceof Authentication auth && auth.getPrincipal() instanceof UserPrincipal up && sessionId != null) {
            presenceService.onConnect(up.getId(), sessionId);
            log.info("PRESENCE CONNECT userId={} session={}", up.getId(), sessionId);
        } else {
            log.warn("CONNECT without UserPrincipal. principal={}, session={}", user, sessionId);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Object user = event.getUser(); // usually Authentication
        String sessionId = event.getSessionId();

        if (user instanceof Authentication auth && auth.getPrincipal() instanceof UserPrincipal up && sessionId != null) {
            presenceService.onDisconnect(up.getId(), sessionId);
            log.info("PRESENCE DISCONNECT userId={} session={}", up.getId(), sessionId);
        } else {
            log.warn("DISCONNECT without UserPrincipal. principal={}, session={}", user, sessionId);
        }
    }
}

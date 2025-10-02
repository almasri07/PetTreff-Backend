package com.socialmedia.petTreff.websocket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class WsEvents {
    @EventListener
    public void onConnect(SessionConnectEvent e) {
        var u = e.getUser();
        log.info("WS CONNECT user={}", u != null ? u.getName() : "null");
    }
    @EventListener
    public void onSubscribe(SessionSubscribeEvent e) {
        log.info("WS SUB user={} dest={}", e.getUser()!=null?e.getUser().getName():"null",
                e.getMessage().getHeaders().get("simpDestination"));
    }
}

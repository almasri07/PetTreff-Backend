package com.socialmedia.petTreff.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // festlegen, welche Prefix unsere Broker besitzt

        registry.enableSimpleBroker("/queue","/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // CONFIGURATIONS FOR  TESTING
        // native websocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "http://localhost:*");

        // browser SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "http://localhost:5173",
                        "http://localhost:*")
                .withSockJS();

  /*   for Dev Purposes
        // festlegen, welche Pfad für WebSocket ist.

        // für Postman und WS clients
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // für browser SockJS clients
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        **/
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {

        DefaultContentTypeResolver myResolver = new DefaultContentTypeResolver();
        myResolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        MappingJackson2MessageConverter myConverter = new MappingJackson2MessageConverter();
        myConverter.setObjectMapper(new ObjectMapper());

        myConverter.setContentTypeResolver(myResolver);

        messageConverters.add(new StringMessageConverter());
        messageConverters.add(new ByteArrayMessageConverter());
        messageConverters.add(myConverter);

          // um sicherstelln, dass unsere Configuration wird
         // nicht ignoriert sondern mit Default Einstellung verwendet werden
        // Rückgabe true sagt dem Compiler nur unsere Config verwendet werden
        return false;
    }
}

package com.socialmedia.petTreff.service;


import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PresenceService {

   //   ConcurrentMap ist Thread-sicher und vermeidet Race-Condition
    private final ConcurrentMap<Long, Instant> onlineUsers = new ConcurrentHashMap<>();

    public void markAsOnline( Long userId) {

        onlineUsers.put(userId, Instant.now());
    }


    public void markAsOffline( Long userId) {

        onlineUsers.remove(userId);
    }

    public boolean isOnline( Long userId ) {

        return onlineUsers.containsKey(userId);
    }

    public Set<Long> allOnlineIds() {

        return onlineUsers.keySet();

    }

}

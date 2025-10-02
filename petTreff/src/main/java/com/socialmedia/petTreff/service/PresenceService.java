package com.socialmedia.petTreff.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service
   public class PresenceService {

       //   ConcurrentMap ist Thread-sicher und vermeidet Race-Condition
       private final ConcurrentMap<Long, Set<String>> sessionsByUser = new ConcurrentHashMap<>();

       public void onConnect(long userId, String sessionId) {
           sessionsByUser.compute(userId, (id, set) -> {
               if (set == null) set = ConcurrentHashMap.newKeySet();
               set.add(sessionId);
               return set;
           });
       }

       public void onDisconnect(long userId, String sessionId) {
           sessionsByUser.computeIfPresent(userId, (id, set) -> {
               set.remove(sessionId);
               return set.isEmpty() ? null : set;
           });
       }

       public boolean isOnline(long userId) {
           return sessionsByUser.containsKey(userId);
       }

       public Set<Long> allOnlineIds() {
           return sessionsByUser.keySet();
       }
   }

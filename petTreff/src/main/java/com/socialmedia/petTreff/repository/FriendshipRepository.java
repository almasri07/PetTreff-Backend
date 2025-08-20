package com.socialmedia.petTreff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialmedia.petTreff.entity.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // testet ob Freundschaft schon existiert
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    // testet ob Freundschaft schon existiert, egal in welcher Richtung
    boolean existsByUserIdAndFriendIdOrFriendIdAndUserId(Long userId, Long friendId, Long friendId2, Long userId2);

    // findet Freundschaft mit Eingabe von Freundschaft-Id und Freund-Id
    Optional<Friendship> findByIdAndFriendId(Long userId, Long friendId);

}

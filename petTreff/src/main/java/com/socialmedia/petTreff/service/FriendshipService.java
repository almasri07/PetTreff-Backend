package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.CreateFriendshipDTO;
import com.socialmedia.petTreff.dto.FriendshipDTO;
import com.socialmedia.petTreff.entity.Friendship;
import com.socialmedia.petTreff.entity.FriendshipStatus;
import com.socialmedia.petTreff.entity.NotificationType;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.FriendshipMapper;
import com.socialmedia.petTreff.repository.FriendshipRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final NotificationService notificationService;

    private final FriendshipRepository friendshipRepository;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<FriendshipDTO> getFriendshipById(Long id) {
        return friendshipRepository.findById(id).map(FriendshipMapper::toDto);
    }

    @Transactional
    public FriendshipDTO createFriendship(CreateFriendshipDTO friendshipDTO, Long requesterId) {

        User recipient = userRepository.findById(friendshipDTO.getFriendId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found."));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getId().equals(requesterId)) {
            throw new AccessDeniedException("Not allowed to send friendship as another user");
        }

        User sender = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        if (requesterId.equals(friendshipDTO.getFriendId())) {

            throw new IllegalArgumentException("Cannot send a request to yourself.");
        }

        boolean isExists = friendshipRepository.existsByUserIdAndFriendIdOrFriendIdAndUserId(requesterId,
                friendshipDTO.getFriendId(), friendshipDTO.getFriendId(), requesterId);

        if (isExists) {
            throw new IllegalArgumentException("Friendship already exists or pending.");
        }

        Friendship friendship = new Friendship();

        friendship.setUser(sender);
        friendship.setFriend(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);

        Friendship saved = friendshipRepository.save(friendship);


       notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.FRIEND_REQUEST,
               "You have a new friend request",
               sender.getUsername() + "has sent you a friend request " ,  sender.getId() , saved.getId() );

        return FriendshipMapper.toDto(saved);
    }

    @Transactional
    public FriendshipDTO accept(Long friendshipId, Long currentUserId) {

        Friendship friendship = friendshipRepository.findByIdAndFriendId(friendshipId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!friendship.getFriend().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to accept for another user");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);



        User accepter = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Accepter not found."));
        User sender = userRepository.findById(friendship.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        accepter.addFriend(sender);
        Friendship saved = friendshipRepository.save(friendship);

        notificationService.create(sender.getId(), sender.getUsername(), NotificationType.FRIEND_REQUEST,
                "Accepted friend request",
                accepter.getUsername() + "has accepted your friend request " , accepter.getId() , saved.getId() );


        return FriendshipMapper.toDto(saved);

    }

    @Transactional
    public FriendshipDTO decline(Long friendshipId, Long currentUserId) {

        Friendship friendship = friendshipRepository.findByIdAndFriendId(friendshipId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!friendship.getFriend().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to decline for another user");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        Friendship saved = friendshipRepository.save(friendship);

        notificationService.create(friendship.getUser().getId(), friendship.getUser().getUsername(), NotificationType.FRIEND_REQUEST,
                "Declined friend request",
                friendship.getFriend().getUsername() + "has declined your friend request " ,  friendship.getFriend().getId() , saved.getId() );


        return FriendshipMapper.toDto(saved);

    }

    @Transactional
    public void deleteFriendship(Long id) {

        Friendship friendship = friendshipRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Request not found."));

        friendship.getUser().removeFriend(friendship.getFriend());

        friendshipRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public String getRelationStatus(Long aId, Long bId) {
        List<Friendship> between = friendshipRepository.findAll().stream()
                .filter(f ->
                        (f.getUser().getId().equals(aId) && f.getFriend().getId().equals(bId)) ||
                                (f.getUser().getId().equals(bId) && f.getFriend().getId().equals(aId))
                )
                .collect(Collectors.toList());

        // 1) gibt es ein PENDING?
        boolean hasPending = between.stream()
                .anyMatch(f -> f.getStatus() == FriendshipStatus.PENDING);
        if (hasPending) return "PENDING";

        // 2) gibt es eine akzeptierte Freundschaft?
        boolean hasFriends = between.stream()
                .anyMatch(f -> f.getStatus() == FriendshipStatus.ACCEPTED);
        if (hasFriends) return "FRIENDS";

        // 3) nichts gefunden
        return "NONE";
    }

}

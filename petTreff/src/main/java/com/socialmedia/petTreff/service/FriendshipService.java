package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.FriendshipDTO;
import com.socialmedia.petTreff.entity.Friendship;
import com.socialmedia.petTreff.entity.FriendshipStatus;
import com.socialmedia.petTreff.mapper.FriendshipMapper;
import com.socialmedia.petTreff.repository.FriendshipRepository;
import com.socialmedia.petTreff.security.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Transactional(readOnly = true)
    public List<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<FriendshipDTO> getFriendshipById(Long id) {
        return friendshipRepository.findById(id).map(FriendshipMapper::toDto);
    }

    @Transactional
    public FriendshipDTO createFriendship(FriendshipDTO friendshipDTO, Long requesterId) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getId().equals(requesterId)) {
            throw new AccessDeniedException("Not allowed to send friendship as another user");
        }
        if (friendshipDTO.getUserId().equals(friendshipDTO.getFriendId())) {

            throw new IllegalArgumentException("Cannot send a request to yourself.");
        }

        boolean isExists = friendshipRepository.existsByUserIdAndFriendIdOrFriendIdAndUserId(friendshipDTO.getUserId(),
                friendshipDTO.getFriendId(), friendshipDTO.getFriendId(), friendshipDTO.getUserId());

        if (isExists) {
            throw new IllegalArgumentException("Friendship already exists or pending.");
        }

        Friendship friendship = FriendshipMapper.toEntity(friendshipDTO);

        friendship.setStatus(FriendshipStatus.PENDING);

        Friendship saved = friendshipRepository.save(friendship);

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

        return FriendshipMapper.toDto(friendshipRepository.save(friendship));

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

        return FriendshipMapper.toDto(friendshipRepository.save(friendship));

    }

    @Transactional
    public void deleteFriendship(Long id) {
        friendshipRepository.deleteById(id);
    }
}

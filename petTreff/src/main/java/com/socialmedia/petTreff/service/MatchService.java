package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.CreateMatchRequestDTO;
import com.socialmedia.petTreff.dto.MatchRequestDTO;
import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.entity.*;
import com.socialmedia.petTreff.repository.MatchInterestRepository;
import com.socialmedia.petTreff.repository.MatchRequestRepository;
import com.socialmedia.petTreff.repository.PetRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final PetRepository petRepository;
    private final MatchRequestRepository requestRepository;
    private final MatchInterestRepository interestRepository;

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    @Transactional
    public MatchRequestDTO createMatchRequest(CreateMatchRequestDTO dto, UserPrincipal userPrincipal) {

        if (requestRepository.existsByAuthorIdAndStatus(userPrincipal.getId(), MatchRequestStatus.OPEN)) {
            throw new IllegalStateException("you already have an open match request with this PET.");
        }

        MatchRequest request = new MatchRequest();

        request.setPetType(dto.getPetType());
        request.setLocation(dto.getLocation());
        request.setDescription(dto.getDescription());
        request.setAuthorId(userPrincipal.getId());
        request.setAuthorUsername(userPrincipal.getUsername());
        request.setStatus(MatchRequestStatus.OPEN);

        MatchRequest saveRequest = requestRepository.save(request);

        return toDto(saveRequest);

    }

    @Transactional(readOnly = true)
    public List<MatchRequestDTO> getRecentRequests(int limit) {

        return requestRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .map(this::toDto)
                .toList();

    }

    @Transactional
    public void sendInterest(Long matchReqId, UserPrincipal sender) {

        // Exception, um nicht verfügbare Requests zu vermeiden
        MatchRequest request = requestRepository.findByIdAndStatus(matchReqId, MatchRequestStatus.OPEN)
                .orElseThrow(() -> new IllegalArgumentException("Request not open or not found !"));

        // Exception wenn der Nutzer Interesse auf sein Request hat
        if (request.getAuthorId().equals(sender.getId())) {
            throw new IllegalArgumentException("You cannot send interest to your own request.");
        }

        // Wenn der Nutzer hat schon Interesse auf diesen Request geschickt
        if (interestRepository.existsByMatchRequestIdAndSenderIdAndStatus(request.getId(), sender.getId(),
                InterestStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending request.");
        }

        MatchInterest interest = new MatchInterest();

        interest.setMatchRequestId(request.getId());
        interest.setSenderId(sender.getId());
        interest.setSenderUsername(sender.getUsername());
        interest.setStatus(InterestStatus.PENDING);

        MatchInterest saved = interestRepository.save(interest);

        User recipient = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found !"));


        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.MATCH_INTEREST,
                "Someone sent you a match interest",
                sender.getUsername() + " has sent you a match interest", sender.getId(), saved.getId());

    }

    @Transactional
    public void accept(Long interestId, UserPrincipal current) {
        MatchInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
        MatchRequest request = requestRepository.findById(interest.getMatchRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        // nur der request-Besitzer kann das Request akzeptieren
        if (!request.getAuthorId().equals(current.getId())) {
            throw new SecurityException("Only the receiver (owner) can accept.");
        }

        if (request.getStatus() == MatchRequestStatus.ACCEPTED &&
                !Objects.equals(request.getAcceptedInterestId(), interestId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already accepted by another interest");
        }

        if (interest.getStatus() == InterestStatus.ACCEPTED) {
            // Ensure request reflects that acceptance
            if (request.getStatus() != MatchRequestStatus.ACCEPTED ||
                    !Objects.equals(request.getAcceptedInterestId(), interestId)) {
                request.setStatus(MatchRequestStatus.ACCEPTED);
                request.setAcceptedInterestId(interestId);
                requestRepository.save(request);
            }
            return; // done
        }

        if (interest.getStatus() != InterestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Interest is not pending");
        }

        interest.setStatus(InterestStatus.ACCEPTED);
        interestRepository.save(interest);

        request.setStatus(MatchRequestStatus.ACCEPTED);
        request.setAcceptedInterestId(interestId);
        requestRepository.save(request);

        MatchInterest saved = interestRepository.save(interest);
        requestRepository.save(request);

        User recipient = userRepository.findById(interest.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found !"));

        User accepter = userRepository.findById(current.getId())
                .orElseThrow(() -> new IllegalArgumentException("Accepter not found !"));

        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.MATCH_INTEREST_ACCEPTED,
                "Someone accepted your interest",
                accepter.getUsername() + " has accepted your match interest", accepter.getId(), saved.getId());


    }

    @Transactional
    public void decline(Long interestId, UserPrincipal current) {
        MatchInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
        MatchRequest request = requestRepository.findById(interest.getMatchRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getAuthorId().equals(current.getId())) {
            throw new SecurityException("Only the receiver (owner) can decline.");
        }
        if (interest.getStatus() == InterestStatus.PENDING) {
            interest.setStatus(InterestStatus.DECLINED);
        }

        MatchInterest saved = interestRepository.save(interest);
        requestRepository.save(request);

        User recipient = userRepository.findById(interest.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found !"));

        User decliner = userRepository.findById(current.getId())
                .orElseThrow(() -> new IllegalArgumentException("Decliner not found !"));

        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.MATCH_INTEREST_DECLINED,
                "Someone declined your interest",
                decliner.getUsername() + " has declined your match interest", decliner.getId(), saved.getId());


    }

    @Transactional
    public void closeRequest(Long requestId, UserPrincipal current) {
        MatchRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!request.getAuthorId().equals(current.getId())) {
            throw new SecurityException("Only the owner can close their request.");
        }
        request.setStatus(MatchRequestStatus.CLOSED);
        requestRepository.save(request);

    }

    @Transactional
    public void deleteRequest(Long id, @AuthenticationPrincipal UserPrincipal principal) {

        MatchRequest ReqToDelete = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match Request not found with id: " + id));

        if (!ReqToDelete.getAuthorId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to delete this Request");
        }

        requestRepository.deleteById(id);
    }

    @Transactional
    public void adminDeleteRequest(Long id) {
        var request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match request not found"));

        requestRepository.delete(request);

    }


    @Transactional
    public void deleteInterest(Long id) {
        MatchInterest InterestToDelete = interestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match Interest not found with id: " + id));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!InterestToDelete.getSenderId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to delete this Interest");
        }

        interestRepository.deleteById(id);

    }


    private MatchRequestDTO toDto(MatchRequest r) {

        MatchRequestDTO matchRequestDTO = new MatchRequestDTO();

        matchRequestDTO.setId(r.getId());
        matchRequestDTO.setPetType(r.getPetType());
        matchRequestDTO.setLocation(r.getLocation());
        matchRequestDTO.setDescription(r.getDescription());
        matchRequestDTO.setAuthorId(r.getAuthorId());
        matchRequestDTO.setAuthorUsername(r.getAuthorUsername());
        matchRequestDTO.setCreatedAt(r.getCreatedAt());
        matchRequestDTO.setStatus(r.getStatus());
        matchRequestDTO.setAcceptedInterestId(r.getAcceptedInterestId());


        return matchRequestDTO;
    }

/*
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String,Object>> findAcceptedPeer(Long matchId, Long requesterId) {
        var mr = requestRepository.findByIdAndStatus(matchId, MatchRequestStatus.ACCEPTED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found or not ACCEPTED"));

        log.info("accepted-peer: matchId={}, requesterId={}, authorId={}, acceptedInterestId={}",
                matchId, requesterId, mr.getAuthorId(), mr.getAcceptedInterestId());

        if (mr.getAcceptedInterestId() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "acceptedInterestId is null");
        }

        var interest = interestRepository.findById(mr.getAcceptedInterestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accepted interest not found"));

        final Long peerId;
        if (Objects.equals(mr.getAuthorId(), requesterId)) {
            peerId = interest.getSenderId();           // author -> peer is sender
        } else if (Objects.equals(interest.getSenderId(), requesterId)) {
            peerId = mr.getAuthorId();                 // sender -> peer is author
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a participant");
        }

        var peer = userRepository.findById(peerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peer user not found"));

        return ResponseEntity.ok(Map.of(
                "id", peer.getId(),
                "username", peer.getUsername(),
                "profilePicture", peer.getProfilePictureUrl()
        ));
    }


*/
@Transactional(readOnly = true)
public ResponseEntity<UserDTO> findAcceptedPeer(Long matchId, Long requesterId) {
    var mr = requestRepository.findByIdAndStatus(matchId, MatchRequestStatus.ACCEPTED)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found or not ACCEPTED"));

    if (mr.getAcceptedInterestId() == null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "acceptedInterestId is null");
    }

    var interest = interestRepository.findById(mr.getAcceptedInterestId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accepted interest not found"));

    final Long peerId =
            Objects.equals(mr.getAuthorId(), requesterId) ? interest.getSenderId() :
                    Objects.equals(interest.getSenderId(), requesterId) ? mr.getAuthorId() : null;

    if (peerId == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a participant");

    var peer = userRepository.findById(peerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peer user not found"));

    var dto = new UserDTO();
    dto.setId(peer.getId());
    dto.setUsername(peer.getUsername());
    dto.setProfilePictureUrl(peer.getProfilePictureUrl()); // may be null; fine

    return ResponseEntity.ok(dto);
}


    public Optional<Long> getCurrentMatchIdForUser(Long userId) {
        // If I'm the author of an ACCEPTED request → return it
        Optional<Long> byAuthor = requestRepository
                .findByAuthorIdAndStatus(userId, MatchRequestStatus.ACCEPTED)
                .map(MatchRequest::getId);

        if (byAuthor.isPresent()) return byAuthor;

        // Else, if I'm the accepted interest sender → return that request
        return requestRepository
                .findAcceptedByInterestSenderId(userId, MatchRequestStatus.ACCEPTED)
                .map(MatchRequest::getId);
    }

}


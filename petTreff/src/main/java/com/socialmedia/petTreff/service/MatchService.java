package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.CreateMatchRequestDTO;
import com.socialmedia.petTreff.dto.MatchRequestDTO;
import com.socialmedia.petTreff.entity.*;
import com.socialmedia.petTreff.repository.MatchInterestRepository;
import com.socialmedia.petTreff.repository.MatchRequestRepository;
import com.socialmedia.petTreff.repository.PetRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        if (requestRepository.existsByAuthorIdAndStatus(userPrincipal.getId(),  MatchRequestStatus.OPEN)) {
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

        MatchInterest  saved = interestRepository.save(interest);

        User recipient = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found !"));


        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.MATCH,
                "Someone sent you a match interest",
                sender.getUsername() + "has sent you a match interest" ,  sender.getId() , saved.getId() );

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
        if (interest.getStatus() != InterestStatus.PENDING)
            return;

        interest.setStatus(InterestStatus.ACCEPTED);
        // schließ das Request beim ersten Akzeptieren
        request.setStatus(MatchRequestStatus.ACCEPTED);

        MatchInterest saved = interestRepository.save(interest);
        requestRepository.save(request);

        User recipient = userRepository.findById(interest.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found !"));

        User accepter = userRepository.findById(current.getId())
                .orElseThrow(() -> new IllegalArgumentException("Accepter not found !"));

        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.MATCH,
                "Someone accepted your interest",
                accepter.getUsername() + "has accepted your match interest" ,  accepter.getId() , saved.getId() );


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

        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.MATCH,
                "Someone declined your interest",
                decliner.getUsername() + "has declined your match interest" ,  decliner.getId() , saved.getId() );



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
    public void deleteRequest(Long id) {

            MatchRequest ReqToDelete = requestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Match Request not found with id: " + id));

            UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!ReqToDelete.getAuthorId().equals(principal.getId())) {
                throw new AccessDeniedException("Not allowed to delete this Request");
            }

            requestRepository.deleteById(id);
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

        return matchRequestDTO;
    }


}

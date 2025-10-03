package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.MatchRequest;
import com.socialmedia.petTreff.entity.MatchRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    Page<MatchRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByAuthorIdAndStatus(Long authorId, MatchRequestStatus status);

    Optional<MatchRequest> findByAuthorIdAndStatus(Long authorId, MatchRequestStatus status);

    Optional<MatchRequest> findByIdAndStatus(Long id, MatchRequestStatus status);

    @Query("""
       select mr
       from MatchRequest mr
       where mr.status = :status
         and mr.acceptedInterestId in (
             select i.id from MatchInterest i where i.senderId = :senderId
         )
    """)
    Optional<MatchRequest> findAcceptedByInterestSenderId(Long senderId, MatchRequestStatus status);

}

package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.InterestStatus;
import com.socialmedia.petTreff.entity.MatchInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchInterestRepository extends JpaRepository<MatchInterest, Long> {

    boolean existsByMatchRequestIdAndSenderIdAndStatus(Long reqId, Long senderId, InterestStatus status);

    List<MatchInterest> findTop10ByMatchRequestIdOrderByCreatedAtDesc(Long reqId);

    Optional<MatchInterest> findFirstByMatchRequestIdAndStatus(Long id, InterestStatus accepted);
}

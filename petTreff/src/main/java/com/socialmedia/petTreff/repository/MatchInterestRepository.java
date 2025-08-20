package com.socialmedia.petTreff.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialmedia.petTreff.entity.InterestStatus;
import com.socialmedia.petTreff.entity.MatchInterest;

public interface MatchInterestRepository extends JpaRepository<MatchInterest, Long> {

    boolean existsByMatchRequestIdAndSenderIdAndStatus(Long reqId, Long senderId, InterestStatus status);

    List<MatchInterest> findTop10ByMatchRequestIdOrderByCreatedAtDesc(Long reqId);

}

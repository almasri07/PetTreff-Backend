package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsernameContainingIgnoreCase(String query);

    Optional<User> findByUsername(String username);

    boolean existsByAuthoritiesAuthority(String authority);


    Page<User> findAllByOrderByIdAsc(Pageable pageable);

    List<User> findByAuthoritiesAuthority(String authority);


    Optional<Object> findByEmail(String email);
}

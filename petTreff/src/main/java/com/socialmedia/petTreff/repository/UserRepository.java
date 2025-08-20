package com.socialmedia.petTreff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialmedia.petTreff.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsernameContainingIgnoreCase(String query);

    Optional<User> findByUsername(String username);

}

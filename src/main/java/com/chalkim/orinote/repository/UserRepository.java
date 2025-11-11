package com.chalkim.orinote.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chalkim.orinote.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySub(String sub);
}

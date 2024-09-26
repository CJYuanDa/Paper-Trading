package com.example.paper.trading.repository;

import com.example.paper.trading.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String userEmail);
}

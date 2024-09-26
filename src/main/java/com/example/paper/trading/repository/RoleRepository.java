package com.example.paper.trading.repository;

import com.example.paper.trading.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByName(String name);
}

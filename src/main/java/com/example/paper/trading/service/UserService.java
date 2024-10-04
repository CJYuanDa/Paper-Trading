package com.example.paper.trading.service;

import com.example.paper.trading.dataTransferObject.UserDTO;
import com.example.paper.trading.model.Users;
import com.example.paper.trading.repository.RoleRepository;
import com.example.paper.trading.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<Map<String, String>> register(UserDTO userDTO) {
        if (userDTO == null) {
            throw new NullPointerException("The student Dto should not be null");
        }

        // Create a response map
        Map<String, String> response = new HashMap<>();

        // Check is email existed
        Optional<Users> checkEmail = userRepository.findByEmail(userDTO.email());
        if (checkEmail.isPresent()) {
            response.put("error", "This email is already existed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        // hash the password (bcrypt) and create the user
        String hashPassword = passwordEncoder.encode(userDTO.password());
        var registerDate = new Date(System.currentTimeMillis());
        var defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Default role not found"));

        Users user = new Users(
                userDTO.firstName(),
                userDTO.lastName(),
                userDTO.email(),
                hashPassword,
                userDTO.dateOfBirth(),
                registerDate,
                false,
                Set.of(defaultRole)
        );

        // save the user to the database
        Users saveUser = userRepository.save(user);
        if (saveUser.getId() > 0) {
            response.put("message", "Successfully registered");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        throw new RuntimeException("User registration failed");
    }

    public ResponseEntity<Map<String, String>> headerLogin(Authentication authentication) {
        Optional<Users> user = userRepository.findByEmail(authentication.getName());
        return null;
    }
}

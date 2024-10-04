package com.example.paper.trading.service;

import com.example.paper.trading.dataTransferObject.ApiLoginDTO;
import com.example.paper.trading.dataTransferObject.UserDTO;
import com.example.paper.trading.model.Users;
import com.example.paper.trading.repository.RoleRepository;
import com.example.paper.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> register(UserDTO userDTO) {
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

    public ResponseEntity<?> headerLogin(Authentication authentication) {
        Users user = userRepository.findByEmail(authentication.getName()).orElseThrow(() ->
                new RuntimeException("user service: header login -> can not find user"));

        Map<String, String> response = new HashMap<>();
        response.put("first name", user.getFirstName());
        response.put("email", user.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> apiLogin(ApiLoginDTO apiLoginDTO) {
        Authentication unauthentication = UsernamePasswordAuthenticationToken
                .unauthenticated(apiLoginDTO.username(), apiLoginDTO.password());
        Authentication authenticationResponse = authenticationManager.authenticate(unauthentication);

        if (authenticationResponse == null || !authenticationResponse.isAuthenticated()) {
            throw new RuntimeException("user service: api login -> authenticated failed");
        }

        String jwt = jwtService.generate(authenticationResponse);

        Optional<Users> user = userRepository.findByEmail(authenticationResponse.getName());
        if (user.isEmpty()) throw new RuntimeException("user service: api login -> can not find user");
        Map<String, String> response = new HashMap<>();
        response.put("first name", user.get().getFirstName());
        response.put("email", user.get().getEmail());

        return ResponseEntity.status(HttpStatus.OK).header(jwtService.getJWTHeader(), jwt).body(response);
    }
}

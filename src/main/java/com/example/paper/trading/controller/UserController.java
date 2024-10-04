package com.example.paper.trading.controller;

import com.example.paper.trading.dataTransferObject.ApiLoginDTO;
import com.example.paper.trading.dataTransferObject.UserDTO;
import com.example.paper.trading.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @RequestMapping("/header-login")
    public ResponseEntity<?> headerLogin(Authentication authentication) {
        return userService.headerLogin(authentication);
    }

    @PostMapping("/api-login")
    public ResponseEntity<?> apiLogin(@Valid @RequestBody ApiLoginDTO apiLoginDTO) {
        return userService.apiLogin(apiLoginDTO);
    }
}

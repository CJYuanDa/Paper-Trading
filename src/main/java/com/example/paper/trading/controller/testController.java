package com.example.paper.trading.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/role-user")
    public String testUser() {
        return "user layer";
    }

    @GetMapping("/role-admin")
    public String testAdin() {
        return "admin layer";
    }

    @GetMapping("/")
    public String test() {
        return "test 12345";
    }
}

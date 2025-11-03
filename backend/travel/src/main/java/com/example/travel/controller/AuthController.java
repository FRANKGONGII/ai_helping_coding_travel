package com.example.travel.controller;

import com.example.travel.entity.User;
import com.example.travel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username,
                                       @RequestParam String password,
                                       @RequestParam String email) {
        try {
            User user = userService.registerUser(username, password, email);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                    @RequestParam String password) {
        Optional<User> userOptional = userService.loginUser(username, password);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
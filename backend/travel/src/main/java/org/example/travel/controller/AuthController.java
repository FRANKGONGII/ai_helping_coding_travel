package org.example.travel.controller;

import org.example.travel.entity.User;
import org.example.travel.service.UserService;
import org.example.travel.dto.LoginRequest;
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
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail());
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (userOptional.isPresent()) {
            System.out.println(userOptional.get().getId());
            return ResponseEntity.ok(userOptional.get().getId());
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 清除令牌逻辑（如果需要，可以扩展为黑名单机制）
        return ResponseEntity.ok("Logged out successfully");
    }
}
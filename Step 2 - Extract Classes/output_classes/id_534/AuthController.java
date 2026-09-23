package com.openisle.controller;

import com.openisle.model.User;
import com.openisle.service.EmailService;
import com.openisle.service.JwtService;
import com.openisle.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req.getUsername(), req.getEmail(), req.getPassword());
        emailService.sendEmail(user.getEmail(), "Welcome to OpenIsle", "Thank you for registering.");
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userService.authenticate(req.getUsername(), req.getPassword())
                .map(user -> ResponseEntity.ok(new JwtResponse(jwtService.generateToken(user.getUsername()))))
                .orElse(ResponseEntity.status(401).build());
    }

    @Data
    private static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data
    private static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    private static class JwtResponse {
        private final String token;
    }
}

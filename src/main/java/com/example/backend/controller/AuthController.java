package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.encoder  = encoder;
        this.jwtUtil  = jwtUtil;
    }

    // ---- REGISTER ----
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }
        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(User.Role.valueOf(req.role()));
        userRepo.save(user);
        return ResponseEntity.ok("Registered successfully.");
    }

    // ---- LOGIN ----
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userRepo.findByEmail(req.email())
                .orElse(null);
        if (user == null || !encoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials.");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role",  user.getRole().name(),
                "name",  user.getName(),
                "email", user.getEmail(),
                "id",    user.getId()
        ));
    }

    record RegisterRequest(String name, String email, String password, String role) {}
    record LoginRequest(String email, String password) {}
}

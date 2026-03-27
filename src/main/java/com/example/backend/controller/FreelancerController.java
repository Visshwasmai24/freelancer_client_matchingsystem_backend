package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/freelancers")
public class FreelancerController {

    private final UserRepository userRepository;

    public FreelancerController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Browse all freelancers (for clients to invite)
    @GetMapping
    public List<User> getAllFreelancers() {
        return userRepository.findByRole(User.Role.FREELANCER);
    }

    // Get single freelancer profile
    @GetMapping("/{id}")
    public ResponseEntity<User> getFreelancer(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update skills
    @PutMapping("/{id}/skills")
    public ResponseEntity<?> updateSkills(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            user.setSkills(body.get("skills"));
            userRepository.save(user);
            return ResponseEntity.ok("Skills updated.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Update full profile (skills, portfolio, hourly rate)
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            if (body.containsKey("skills"))    user.setSkills(body.get("skills"));
            if (body.containsKey("portfolio")) user.setPortfolio(body.get("portfolio"));
            if (body.containsKey("hourlyRate") && body.get("hourlyRate") != null && !body.get("hourlyRate").isBlank()) {
                user.setHourlyRate(Double.parseDouble(body.get("hourlyRate")));
            }
            userRepository.save(user);
            return ResponseEntity.ok("Profile updated.");
        }).orElse(ResponseEntity.notFound().build());
    }
}

package com.example.backend.controller;

import com.example.backend.repository.UserRepository;
import com.example.backend.service.ResumeParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeParserService resumeParserService;
    private final UserRepository userRepository;

    public ResumeController(ResumeParserService resumeParserService,
                            UserRepository userRepository) {
        this.resumeParserService = resumeParserService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("freelancerId") Long freelancerId) {

        if (file.isEmpty()) return ResponseEntity.badRequest().body("No file provided.");

        try {
            Map<String, Object> result = resumeParserService.extractSkillsWithInfo(file);

            @SuppressWarnings("unchecked")
            List<String> skills = (List<String>) result.get("skills");

            if (skills != null && !skills.isEmpty()) {
                userRepository.findById(freelancerId).ifPresent(user -> {
                    user.setSkills(String.join(", ", skills));
                    userRepository.save(user);
                });
            }

            return ResponseEntity.ok(Map.of(
                "skills",       skills,
                "isImageBased", result.get("isImageBased"),
                "message",      result.get("message")
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to process resume: " + e.getMessage());
        }
    }
}
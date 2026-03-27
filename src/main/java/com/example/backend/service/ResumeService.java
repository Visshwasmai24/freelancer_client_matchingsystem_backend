package com.example.backend.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

@Service
public class ResumeService {

    private final ResumeParserService resumeParserService;
    private final UserRepository userRepository;

    public ResumeService(ResumeParserService resumeParserService, UserRepository userRepository) {
        this.resumeParserService = resumeParserService;
        this.userRepository = userRepository;
    }

    /**
     * Parse an uploaded resume, store the extracted skills on the freelancer record,
     * and return a simple response payload.
     */
    public Map<String, Object> parseAndSave(MultipartFile file, Long freelancerId) throws IOException {
        List<String> skills = resumeParserService.extractSkills(file);
        String skillsCsv = String.join(", ", skills);

        User freelancer = userRepository.findById(freelancerId)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        freelancer.setSkills(skillsCsv);
        userRepository.save(freelancer);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("freelancerId", freelancerId);
        result.put("skills", skills);
        return result;
    }
}

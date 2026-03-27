package com.example.backend.service;

import com.example.backend.model.Project;
import com.example.backend.model.User;
import com.example.backend.repository.ProjectRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public MatchService(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * For a FREELANCER: return all projects ranked by match score.
     * matchScore = (matched skills / required skills) * 100
     */
    public List<Map<String, Object>> matchProjectsForFreelancer(Long freelancerId) {
        User freelancer = userRepository.findById(freelancerId)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        Set<String> freelancerSkills = parseSkills(freelancer.getSkills());

        return projectRepository.findAll().stream()
                .filter(p -> p.getApprovalStatus() == Project.ApprovalStatus.APPROVED)
                .map(project -> {
                    Set<String> required = parseSkills(project.getRequiredSkills());
                    List<String> matched = freelancerSkills.stream()
                            .filter(required::contains)
                            .collect(Collectors.toList());

                    int score = required.isEmpty() ? 0 :
                            (int) Math.round((double) matched.size() / required.size() * 100);

                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("project", project);
                    result.put("matchScore", score);
                    result.put("matchedSkills", matched);
                    return result;
                })
                .filter(r -> (int) r.get("matchScore") > 0)
                .sorted((a, b) -> (int) b.get("matchScore") - (int) a.get("matchScore"))
                .collect(Collectors.toList());
    }

    /**
     * For a CLIENT: given a project, return freelancers ranked by match score.
     */
    public List<Map<String, Object>> matchFreelancersForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Set<String> required = parseSkills(project.getRequiredSkills());

        return userRepository.findByRole(User.Role.FREELANCER).stream()
                .map(freelancer -> {
                    Set<String> fSkills = parseSkills(freelancer.getSkills());
                    List<String> matched = required.stream()
                            .filter(fSkills::contains)
                            .collect(Collectors.toList());

                    int score = required.isEmpty() ? 0 :
                            (int) Math.round((double) matched.size() / required.size() * 100);

                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("freelancer", sanitizeUser(freelancer));
                    result.put("matchScore", score);
                    result.put("matchedSkills", matched);
                    return result;
                })
                .filter(r -> (int) r.get("matchScore") > 0)
                .sorted((a, b) -> (int) b.get("matchScore") - (int) a.get("matchScore"))
                .collect(Collectors.toList());
    }

    // ---- Helpers ----

    private Set<String> parseSkills(String skills) {
        if (skills == null || skills.isBlank()) return Set.of();
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> u = new LinkedHashMap<>();
        u.put("id", user.getId());
        u.put("name", user.getName());
        u.put("email", user.getEmail());
        u.put("skills", user.getSkills());
        u.put("portfolio", user.getPortfolio());
        u.put("hourlyRate", user.getHourlyRate());
        u.put("role", user.getRole());
        return u;
    }
}

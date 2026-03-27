package com.example.backend.controller;

import com.example.backend.service.MatchService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /** Projects ranked for a specific freelancer */
    @GetMapping("/freelancer/{freelancerId}")
    public List<Map<String, Object>> forFreelancer(@PathVariable Long freelancerId) {
        return matchService.matchProjectsForFreelancer(freelancerId);
    }

    /** Freelancers ranked for a specific project */
    @GetMapping("/project/{projectId}")
    public List<Map<String, Object>> forProject(@PathVariable Long projectId) {
        return matchService.matchFreelancersForProject(projectId);
    }
}

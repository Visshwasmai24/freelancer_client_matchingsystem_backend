package com.example.backend.controller;

import com.example.backend.model.Project;
import com.example.backend.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // All APPROVED projects (with optional filters)
    @GetMapping
    public List<Project> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double maxBudget) {

        return projectRepository.findAll().stream()
                .filter(p -> p.getApprovalStatus() == Project.ApprovalStatus.APPROVED)
                .filter(p -> category == null || category.isBlank() || category.equalsIgnoreCase(p.getCategory()))
                .filter(p -> maxBudget == null || (p.getBudget() != null && p.getBudget() <= maxBudget))
                .collect(Collectors.toList());
    }

    // Projects by client
    @GetMapping("/client/{clientId}")
    public List<Project> getByClient(@PathVariable Long clientId) {
        return projectRepository.findByClientId(clientId);
    }

    // Create project
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectRepository.save(project);
    }

    // Update project status (client marks as complete etc.)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return projectRepository.findById(id).map(p -> {
            p.setStatus(Project.ProjectStatus.valueOf(status.toUpperCase()));
            projectRepository.save(p);
            return ResponseEntity.ok("Status updated.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete project
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!projectRepository.existsById(id)) return ResponseEntity.notFound().build();
        projectRepository.deleteById(id);
        return ResponseEntity.ok("Deleted.");
    }
}

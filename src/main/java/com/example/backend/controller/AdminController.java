package com.example.backend.controller;

import com.example.backend.model.Project;
import com.example.backend.model.User;
import com.example.backend.repository.ProjectRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public AdminController(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    // --- USERS ---
    @GetMapping("/freelancers")
    public List<User> getFreelancers() { return userRepository.findByRole(User.Role.FREELANCER); }

    @GetMapping("/clients")
    public List<User> getClients() { return userRepository.findByRole(User.Role.CLIENT); }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted.");
    }

    // Block a user account
    @PutMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        return userRepository.findById(id).map(u -> {
            u.setAccountStatus(User.AccountStatus.BLOCKED);
            userRepository.save(u);
            return ResponseEntity.ok("User blocked.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Unblock / approve a user account
    @PutMapping("/users/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        return userRepository.findById(id).map(u -> {
            u.setAccountStatus(User.AccountStatus.ACTIVE);
            userRepository.save(u);
            return ResponseEntity.ok("User approved.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- PROJECTS ---
    @GetMapping("/projects")
    public List<Project> getAllProjects() { return projectRepository.findAll(); }

    // Reject a reported/inappropriate project
    @PutMapping("/projects/{id}/reject")
    public ResponseEntity<?> rejectProject(@PathVariable Long id) {
        return projectRepository.findById(id).map(p -> {
            p.setApprovalStatus(Project.ApprovalStatus.REJECTED);
            projectRepository.save(p);
            return ResponseEntity.ok("Project rejected.");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/projects/{id}/approve")
    public ResponseEntity<?> approveProject(@PathVariable Long id) {
        return projectRepository.findById(id).map(p -> {
            p.setApprovalStatus(Project.ApprovalStatus.APPROVED);
            projectRepository.save(p);
            return ResponseEntity.ok("Project approved.");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        if (!projectRepository.existsById(id)) return ResponseEntity.notFound().build();
        projectRepository.deleteById(id);
        return ResponseEntity.ok("Project deleted.");
    }

    // --- SYSTEM REPORTS ---
    @GetMapping("/reports")
    public Map<String, Object> getSystemReport() {
        long totalFreelancers = userRepository.findByRole(User.Role.FREELANCER).size();
        long totalClients     = userRepository.findByRole(User.Role.CLIENT).size();
        long totalProjects    = projectRepository.count();
        long activeProjects   = projectRepository.findAll().stream()
                .filter(p -> p.getStatus() == Project.ProjectStatus.ONGOING).count();
        long completedProjects = projectRepository.findAll().stream()
                .filter(p -> p.getStatus() == Project.ProjectStatus.COMPLETED).count();

        return Map.of(
                "totalFreelancers",  totalFreelancers,
                "totalClients",      totalClients,
                "totalUsers",        totalFreelancers + totalClients,
                "totalProjects",     totalProjects,
                "activeProjects",    activeProjects,
                "completedProjects", completedProjects
        );
    }
}

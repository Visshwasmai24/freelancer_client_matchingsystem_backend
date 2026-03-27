package com.example.backend.controller;

import com.example.backend.model.Project;
import com.example.backend.model.Review;
import com.example.backend.repository.ProjectRepository;
import com.example.backend.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProjectRepository projectRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            ProjectRepository projectRepository) {
        this.reviewRepository = reviewRepository;
        this.projectRepository = projectRepository;
    }

    // Client submits a review → also marks project COMPLETED
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5.");
        }

        reviewRepository.save(review);

        // Mark project as completed
        projectRepository.findById(review.getProjectId()).ifPresent(project -> {
            project.setStatus(Project.ProjectStatus.COMPLETED);
            projectRepository.save(project);
        });

        return ResponseEntity.ok("Review submitted. Project marked as completed.");
    }

    // Get reviews for a freelancer
    @GetMapping("/freelancer/{freelancerId}")
    public List<Review> getFreelancerReviews(@PathVariable Long freelancerId) {
        return reviewRepository.findByFreelancerId(freelancerId);
    }

    // Get review for a project
    @GetMapping("/project/{projectId}")
    public List<Review> getProjectReviews(@PathVariable Long projectId) {
        return reviewRepository.findByProjectId(projectId);
    }
}

package com.example.backend.repository;

import com.example.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFreelancerId(Long freelancerId);
    List<Review> findByProjectId(Long projectId);
}

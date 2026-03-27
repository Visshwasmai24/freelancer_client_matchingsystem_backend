package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private Long freelancerId;
    private String freelancerName;

    private Double bidAmount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ProposalStatus status = ProposalStatus.PENDING;

    private LocalDateTime submittedAt = LocalDateTime.now();

    public enum ProposalStatus { PENDING, ACCEPTED, REJECTED }

    public Proposal() {}

    public Long getId() { return id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getFreelancerId() { return freelancerId; }
    public void setFreelancerId(Long freelancerId) { this.freelancerId = freelancerId; }
    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }
    public Double getBidAmount() { return bidAmount; }
    public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProposalStatus getStatus() { return status; }
    public void setStatus(ProposalStatus status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}

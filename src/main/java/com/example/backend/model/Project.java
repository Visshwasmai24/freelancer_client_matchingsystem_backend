package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requiredSkills;

    private String clientName;
    private Long clientId;
    private Double budget;
    private String category;
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;

    public enum ProjectStatus { PENDING, ONGOING, COMPLETED }
    public enum ApprovalStatus { APPROVED, REJECTED }

    public Project() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String s) { this.requiredSkills = s; }
    public String getClientName() { return clientName; }
    public void setClientName(String n) { this.clientName = n; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long id) { this.clientId = id; }
    public Double getBudget() { return budget; }
    public void setBudget(Double b) { this.budget = b; }
    public String getCategory() { return category; }
    public void setCategory(String c) { this.category = c; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate d) { this.deadline = d; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus s) { this.status = s; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus a) { this.approvalStatus = a; }
}

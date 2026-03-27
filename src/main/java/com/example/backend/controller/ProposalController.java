package com.example.backend.controller;

import com.example.backend.model.Proposal;
import com.example.backend.model.Project;
import com.example.backend.repository.ProposalRepository;
import com.example.backend.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
public class ProposalController {

    private final ProposalRepository proposalRepository;
    private final ProjectRepository projectRepository;

    public ProposalController(ProposalRepository proposalRepository,
                              ProjectRepository projectRepository) {
        this.proposalRepository = proposalRepository;
        this.projectRepository = projectRepository;
    }

    // Freelancer submits proposal
    @PostMapping
    public ResponseEntity<?> submitProposal(@RequestBody Proposal proposal) {
        return ResponseEntity.ok(proposalRepository.save(proposal));
    }

    // Get proposals for a specific project (client views)
    @GetMapping("/project/{projectId}")
    public List<Proposal> getByProject(@PathVariable Long projectId) {
        return proposalRepository.findByProjectId(projectId);
    }

    // Get proposals submitted by a freelancer
    @GetMapping("/freelancer/{freelancerId}")
    public List<Proposal> getByFreelancer(@PathVariable Long freelancerId) {
        return proposalRepository.findByFreelancerId(freelancerId);
    }

    // Client accepts a proposal → sets project to ONGOING
    @PutMapping("/{proposalId}/accept")
    public ResponseEntity<?> acceptProposal(@PathVariable Long proposalId) {
        return proposalRepository.findById(proposalId).map(proposal -> {
            proposal.setStatus(Proposal.ProposalStatus.ACCEPTED);
            proposalRepository.save(proposal);

            // Set project status to ONGOING
            projectRepository.findById(proposal.getProjectId()).ifPresent(project -> {
                project.setStatus(Project.ProjectStatus.ONGOING);
                projectRepository.save(project);
            });

            // Reject all other proposals for this project
            proposalRepository.findByProjectId(proposal.getProjectId()).forEach(p -> {
                if (!p.getId().equals(proposalId) && p.getStatus() == Proposal.ProposalStatus.PENDING) {
                    p.setStatus(Proposal.ProposalStatus.REJECTED);
                    proposalRepository.save(p);
                }
            });

            return ResponseEntity.ok("Proposal accepted. Project is now ongoing.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Client rejects a proposal
    @PutMapping("/{proposalId}/reject")
    public ResponseEntity<?> rejectProposal(@PathVariable Long proposalId) {
        return proposalRepository.findById(proposalId).map(proposal -> {
            proposal.setStatus(Proposal.ProposalStatus.REJECTED);
            proposalRepository.save(proposal);
            return ResponseEntity.ok("Proposal rejected.");
        }).orElse(ResponseEntity.notFound().build());
    }
}

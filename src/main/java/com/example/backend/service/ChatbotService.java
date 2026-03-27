package com.example.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class ChatbotService {

    private final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";
    private final String API_KEY = "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";

    public String getResponse(String userMessage) {
        try {
            WebClient webClient = WebClient.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                    .build();

            String prompt = "<s>[INST] You are a helpful assistant for a freelancer-client matching platform. "
                    + "Help users with questions about finding freelancers, posting projects, submitting proposals, "
                    + "uploading resumes, and using the platform. Keep answers concise and friendly.\n\n"
                    + userMessage + " [/INST]";

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("max_new_tokens", 200);
            parameters.put("temperature", 0.7);
            parameters.put("return_full_text", false);

            Map<String, Object> request = new HashMap<>();
            request.put("inputs", prompt);
            request.put("parameters", parameters);

            List<Map<String, Object>> response = webClient.post()
                    .uri(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response != null && !response.isEmpty()) {
                Object text = response.get(0).get("generated_text");
                if (text != null) {
                    return text.toString().trim();
                }
            }

            return "I'm here to help! Please ask me anything about the platform.";

        } catch (Exception e) {
            // Fallback: rule-based responses for common questions
            String msg = userMessage.toLowerCase();
            if (msg.contains("proposal")) return "To submit a proposal, go to Browse All Jobs, find a PENDING project, click Submit Proposal, enter your bid amount and a description, then click Submit.";
            if (msg.contains("resume")) return "Go to Upload Resume tab, select your PDF file, and click Upload. We'll automatically extract your skills and update your profile!";
            if (msg.contains("match")) return "Matches are calculated based on skill overlap between your profile skills and the project's required skills. The higher the percentage, the better the match!";
            if (msg.contains("project") && msg.contains("post")) return "As a client, go to Post a Project, fill in the title, description, required skills, budget, and deadline, then click Post Project.";
            if (msg.contains("message")) return "Use the Messages tab to chat with freelancers or clients. You can also start a chat from the Find Matches section.";
            if (msg.contains("review")) return "After a project is completed, clients can go to Give Review, enter the Project ID and Freelancer ID, give a star rating, and submit feedback.";
            return "I can help you with proposals, resume uploads, project posting, matching, and messaging. What would you like to know?";
        }
    }
}

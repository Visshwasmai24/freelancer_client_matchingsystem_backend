package com.example.backend.controller;

import com.example.backend.service.ChatbotService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");

        if (message == null || message.trim().isEmpty()) {
            return Map.of("reply", "Please enter a message.");
        }

        String reply = chatbotService.getResponse(message);

        return Map.of("reply", reply);
    }
}

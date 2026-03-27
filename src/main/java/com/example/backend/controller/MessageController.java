package com.example.backend.controller;

import com.example.backend.model.Message;
import com.example.backend.repository.MessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Send a message
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        return ResponseEntity.ok(messageRepository.save(message));
    }

    // Get conversation between two users
    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam Long userId1, @RequestParam Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    // Get all conversations for a user (inbox — deduplicated by other person)
    @GetMapping("/inbox/{userId}")
    public List<Map<String, Object>> getInbox(@PathVariable Long userId) {
        List<Message> all = messageRepository.findAllForUser(userId);

        // Group by the "other" person and take the latest message per conversation
        Map<Long, Message> latestByContact = new LinkedHashMap<>();
        for (Message m : all) {
            Long contactId = m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId();
            latestByContact.putIfAbsent(contactId, m);
        }

        return latestByContact.entrySet().stream().map(entry -> {
            Message m = entry.getValue();
            Map<String, Object> conv = new LinkedHashMap<>();
            conv.put("contactId", entry.getKey());
            conv.put("contactName", m.getSenderId().equals(userId) ? m.getReceiverName() : m.getSenderName());
            conv.put("lastMessage", m.getContent());
            conv.put("sentAt", m.getSentAt());
            conv.put("isRead", m.isRead());
            return conv;
        }).collect(Collectors.toList());
    }

    // Mark messages as read
    @PutMapping("/read")
    public ResponseEntity<?> markRead(@RequestParam Long userId, @RequestParam Long contactId) {
        messageRepository.findConversation(userId, contactId).forEach(m -> {
            if (m.getReceiverId().equals(userId) && !m.isRead()) {
                m.setRead(true);
                messageRepository.save(m);
            }
        });
        return ResponseEntity.ok("Marked as read.");
    }
}

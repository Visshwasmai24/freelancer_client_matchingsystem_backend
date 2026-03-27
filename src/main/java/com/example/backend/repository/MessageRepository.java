package com.example.backend.repository;

import com.example.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get full conversation between two users
    @Query("SELECT m FROM Message m WHERE (m.senderId = :a AND m.receiverId = :b) OR (m.senderId = :b AND m.receiverId = :a) ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("a") Long a, @Param("b") Long b);

    // Get all unique conversations for a user (latest message per conversation)
    @Query("SELECT m FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId ORDER BY m.sentAt DESC")
    List<Message> findAllForUser(@Param("userId") Long userId);
}

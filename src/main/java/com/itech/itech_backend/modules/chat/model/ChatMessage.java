package com.itech.itech_backend.modules.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String receiverId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "is_read")
    private boolean read;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        PRODUCT_SHARE,
        QUOTATION,
        SYSTEM
    }
}

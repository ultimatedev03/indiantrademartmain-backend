package com.itech.itech_backend.modules.support.model;

import com.itech.itech_backend.enums.MessageType;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.buyer.model.Inquiry;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;
    
    // File attachment fields
    private String attachmentUrl;
    private String attachmentName;
    private String attachmentType; // image, document, video, etc.
    private Long attachmentSize; // in bytes
    
    // Message metadata
    private String threadId; // For grouping related messages
    private Long replyToMessageId; // For replies
    
    // Message status
    @Builder.Default
    private boolean isRead = false;
    
    @Builder.Default
    private boolean isDelivered = false;
    
    @Builder.Default
    private boolean isEdited = false;
    
    private LocalDateTime editedAt;
    
    @Builder.Default
    private boolean isDeleted = false;
    
    private LocalDateTime deletedAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime readAt;
    
    private LocalDateTime deliveredAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Convenience methods for backward compatibility
    public Long getSenderId() {
        return sender != null ? sender.getId() : null;
    }
    
    public Long getReceiverId() {
        return receiver != null ? receiver.getId() : null;
    }
}


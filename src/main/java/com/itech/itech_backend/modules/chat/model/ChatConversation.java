package com.itech.itech_backend.modules.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String conversationId;

    @Column(nullable = false)
    private String participant1Id;

    @Column(nullable = false)
    private String participant2Id;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @Column(name = "unread_count_p1")
    private Integer unreadCountP1;

    @Column(name = "unread_count_p2")
    private Integer unreadCountP2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationType type;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "conversationId", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    public enum ConversationType {
        DIRECT,
        GROUP,
        SUPPORT,
        SYSTEM
    }

    public void incrementUnreadCount(String participantId) {
        if (participantId.equals(participant1Id)) {
            unreadCountP1 = (unreadCountP1 == null ? 0 : unreadCountP1) + 1;
        } else if (participantId.equals(participant2Id)) {
            unreadCountP2 = (unreadCountP2 == null ? 0 : unreadCountP2) + 1;
        }
    }

    public void clearUnreadCount(String participantId) {
        if (participantId.equals(participant1Id)) {
            unreadCountP1 = 0;
        } else if (participantId.equals(participant2Id)) {
            unreadCountP2 = 0;
        }
    }

    public void updateLastMessage(ChatMessage message) {
        this.lastMessage = message.getContent();
        this.lastMessageTime = message.getCreatedAt();
        incrementUnreadCount(message.getReceiverId());
    }
}

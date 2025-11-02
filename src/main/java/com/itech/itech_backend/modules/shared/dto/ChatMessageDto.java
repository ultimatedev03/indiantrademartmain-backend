package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.itech.itech_backend.enums.MessageType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String message;
    private Long inquiryId;
    private String messageType;
    private MessageType messageTypeEnum;
    
    // Advanced features
    private String threadId;
    private Long replyToMessageId;
    private ChatMessageDto replyToMessage; // For displaying replied message
    
    // File attachments
    private List<MultipartFile> attachments; // For sending
    private List<ChatAttachmentDto> attachmentList; // For receiving
    
    // Message status
    private boolean isRead;
    private boolean isDelivered;
    private boolean isEdited;
    private boolean isDeleted;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime editedAt;
    
    // Additional metadata
    private String location; // For location messages
    private String contactInfo; // For contact messages
    
    // Real-time features
    private boolean isTyping;
    private Long conversationId;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatAttachmentDto {
        private Long id;
        private String fileName;
        private String originalFileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
        private String formattedSize;
        private String thumbnailUrl;
        private Integer width;
        private Integer height;
        private Integer duration;
        private boolean isProcessed;
    }
}


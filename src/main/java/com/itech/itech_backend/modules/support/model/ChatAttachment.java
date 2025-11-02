package com.itech.itech_backend.modules.support.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false) 
    private String fileUrl;

    @Column(nullable = false)
    private String fileType; // MIME type

    @Column(nullable = false)
    private Long fileSize; // in bytes

    private String thumbnailUrl; // For images/videos

    // File metadata
    private Integer width; // For images
    private Integer height; // For images
    private Integer duration; // For videos/audio in seconds

    // Upload information
    @Column(nullable = false)
    private String uploadedBy; // User email or ID

    @Builder.Default
    private boolean isProcessed = false; // For thumbnail generation, virus scan, etc.

    @Builder.Default
    private boolean isPublic = false; // Whether file can be accessed without auth

    private String processingStatus; // PENDING, PROCESSING, COMPLETED, FAILED

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Helper methods
    public boolean isImage() {
        return fileType != null && fileType.startsWith("image/");
    }

    public boolean isVideo() {
        return fileType != null && fileType.startsWith("video/");
    }

    public boolean isAudio() {
        return fileType != null && fileType.startsWith("audio/");
    }

    public boolean isDocument() {
        return fileType != null && (
            fileType.equals("application/pdf") ||
            fileType.contains("document") ||
            fileType.contains("spreadsheet") ||
            fileType.contains("presentation")
        );
    }

    public String getFormattedSize() {
        if (fileSize == null) return "0 B";
        
        long bytes = fileSize;
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}


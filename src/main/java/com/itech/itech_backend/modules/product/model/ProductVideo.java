package com.itech.itech_backend.modules.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_videos", indexes = {
    @Index(name = "idx_product_video_product", columnList = "product_id"),
    @Index(name = "idx_product_video_status", columnList = "status"),
    @Index(name = "idx_product_video_type", columnList = "video_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(name = "duration")
    private Integer duration; // in seconds

    @Column(name = "width")
    private Integer width; // video width in pixels

    @Column(name = "height")
    private Integer height; // video height in pixels

    @Column(name = "format", length = 50)
    private String format; // mp4, avi, mov, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "video_type", nullable = false)
    private VideoType videoType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private VideoStatus status = VideoStatus.ACTIVE;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "download_count")
    @Builder.Default
    private Long downloadCount = 0L;

    // External video URLs (YouTube, Vimeo, etc.)
    @Column(name = "external_video_id", length = 100)
    private String externalVideoId;

    @Column(name = "external_platform", length = 50)
    private String externalPlatform; // youtube, vimeo, etc.

    // Processing status for uploaded videos
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    @Builder.Default
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    @Column(name = "processing_error", length = 1000)
    private String processingError;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    // Enums
    public enum VideoType {
        PRODUCT_DEMO,           // Product demonstration
        UNBOXING,              // Unboxing video
        TUTORIAL,              // How to use
        REVIEW,                // Product review
        INSTALLATION,          // Installation guide
        COMPARISON,            // Product comparison
        TESTIMONIAL,           // Customer testimonial
        MARKETING,             // Marketing video
        SPECIFICATION,         // Technical specifications
        OTHER                  // Other type
    }

    public enum VideoStatus {
        ACTIVE,
        INACTIVE,
        PROCESSING,
        FAILED,
        DELETED
    }

    public enum ProcessingStatus {
        PENDING,               // Just uploaded, waiting for processing
        PROCESSING,            // Being processed
        COMPLETED,             // Successfully processed
        FAILED,                // Processing failed
        THUMBNAIL_GENERATED,   // Thumbnail generation completed
        COMPRESSED             // Video compression completed
    }

    // Helper methods
    public String getFormattedDuration() {
        if (duration == null) return "00:00";
        
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown";
        
        double size = fileSize.doubleValue();
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }

    public Boolean isExternalVideo() {
        return externalVideoId != null && externalPlatform != null;
    }

    public Boolean isProcessed() {
        return processingStatus == ProcessingStatus.COMPLETED || 
               processingStatus == ProcessingStatus.COMPRESSED;
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount != null ? this.viewCount : 0L) + 1;
    }

    public void incrementDownloadCount() {
        this.downloadCount = (this.downloadCount != null ? this.downloadCount : 0L) + 1;
    }
}

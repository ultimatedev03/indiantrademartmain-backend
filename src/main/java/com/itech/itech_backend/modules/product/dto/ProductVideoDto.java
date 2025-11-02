package com.itech.itech_backend.modules.product.dto;

import com.itech.itech_backend.modules.product.model.ProductVideo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVideoDto {

    private Long id;
    private Long productId;
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private String fileName;
    private Long fileSize;
    private String formattedFileSize;
    private Integer duration;
    private String formattedDuration;
    private Integer width;
    private Integer height;
    private String format;
    private String videoType;
    private String status;
    private Boolean isPrimary;
    private Integer sortOrder;
    private Long viewCount;
    private Long downloadCount;
    private String externalVideoId;
    private String externalPlatform;
    private String processingStatus;
    private String processingError;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String uploadedBy;

    // Helper field for frontend
    private Boolean isExternalVideo;
    private Boolean isProcessed;
    private String videoTypeDisplayName;
    private String statusDisplayName;

    public static ProductVideoDto fromEntity(ProductVideo productVideo) {
        if (productVideo == null) {
            return null;
        }

        return ProductVideoDto.builder()
                .id(productVideo.getId())
                .productId(productVideo.getProduct() != null ? productVideo.getProduct().getId() : null)
                .title(productVideo.getTitle())
                .description(productVideo.getDescription())
                .videoUrl(productVideo.getVideoUrl())
                .thumbnailUrl(productVideo.getThumbnailUrl())
                .fileName(productVideo.getFileName())
                .fileSize(productVideo.getFileSize())
                .formattedFileSize(productVideo.getFormattedFileSize())
                .duration(productVideo.getDuration())
                .formattedDuration(productVideo.getFormattedDuration())
                .width(productVideo.getWidth())
                .height(productVideo.getHeight())
                .format(productVideo.getFormat())
                .videoType(productVideo.getVideoType() != null ? productVideo.getVideoType().name() : null)
                .status(productVideo.getStatus() != null ? productVideo.getStatus().name() : null)
                .isPrimary(productVideo.getIsPrimary())
                .sortOrder(productVideo.getSortOrder())
                .viewCount(productVideo.getViewCount())
                .downloadCount(productVideo.getDownloadCount())
                .externalVideoId(productVideo.getExternalVideoId())
                .externalPlatform(productVideo.getExternalPlatform())
                .processingStatus(productVideo.getProcessingStatus() != null ? productVideo.getProcessingStatus().name() : null)
                .processingError(productVideo.getProcessingError())
                .createdAt(productVideo.getCreatedAt())
                .updatedAt(productVideo.getUpdatedAt())
                .uploadedBy(productVideo.getUploadedBy())
                .isExternalVideo(productVideo.isExternalVideo())
                .isProcessed(productVideo.isProcessed())
                .videoTypeDisplayName(getVideoTypeDisplayName(productVideo.getVideoType()))
                .statusDisplayName(getStatusDisplayName(productVideo.getStatus()))
                .build();
    }

    private static String getVideoTypeDisplayName(ProductVideo.VideoType videoType) {
        if (videoType == null) return "Unknown";
        
        switch (videoType) {
            case PRODUCT_DEMO: return "Product Demo";
            case UNBOXING: return "Unboxing";
            case TUTORIAL: return "Tutorial";
            case REVIEW: return "Review";
            case INSTALLATION: return "Installation Guide";
            case COMPARISON: return "Comparison";
            case TESTIMONIAL: return "Testimonial";
            case MARKETING: return "Marketing";
            case SPECIFICATION: return "Specifications";
            case OTHER: return "Other";
            default: return videoType.name();
        }
    }

    private static String getStatusDisplayName(ProductVideo.VideoStatus status) {
        if (status == null) return "Unknown";
        
        switch (status) {
            case ACTIVE: return "Active";
            case INACTIVE: return "Inactive";
            case PROCESSING: return "Processing";
            case FAILED: return "Failed";
            case DELETED: return "Deleted";
            default: return status.name();
        }
    }
}

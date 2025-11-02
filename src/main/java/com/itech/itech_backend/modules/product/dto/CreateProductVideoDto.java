package com.itech.itech_backend.modules.product.dto;

import com.itech.itech_backend.modules.product.model.ProductVideo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductVideoDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Video URL is required")
    @Size(max = 500, message = "Video URL must not exceed 500 characters")
    private String videoUrl;

    @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters")
    private String thumbnailUrl;

    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @Min(value = 0, message = "File size must be non-negative")
    private Long fileSize;

    @Min(value = 0, message = "Duration must be non-negative")
    private Integer duration;

    @Min(value = 0, message = "Width must be non-negative")
    private Integer width;

    @Min(value = 0, message = "Height must be non-negative")
    private Integer height;

    @Size(max = 50, message = "Format must not exceed 50 characters")
    private String format;

    @NotNull(message = "Video type is required")
    private String videoType; // Will be converted to enum

    @Builder.Default
    private Boolean isPrimary = false;

    @Min(value = 0, message = "Sort order must be non-negative")
    @Builder.Default
    private Integer sortOrder = 0;

    // External video fields
    @Size(max = 100, message = "External video ID must not exceed 100 characters")
    private String externalVideoId;

    @Size(max = 50, message = "External platform must not exceed 50 characters")
    private String externalPlatform;

    @Size(max = 100, message = "Uploaded by must not exceed 100 characters")
    private String uploadedBy;

    // Helper method to get VideoType enum
    public ProductVideo.VideoType getVideoTypeEnum() {
        if (videoType == null) {
            return ProductVideo.VideoType.OTHER;
        }
        
        try {
            return ProductVideo.VideoType.valueOf(videoType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProductVideo.VideoType.OTHER;
        }
    }

    // Validation method
    public boolean isValidVideoType() {
        if (videoType == null) return false;
        
        try {
            ProductVideo.VideoType.valueOf(videoType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Check if this is an external video
    public boolean isExternalVideo() {
        return externalVideoId != null && externalPlatform != null;
    }
}

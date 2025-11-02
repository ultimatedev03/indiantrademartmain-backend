package com.itech.itech_backend.modules.product.service;

import com.itech.itech_backend.modules.product.dto.CreateProductVideoDto;
import com.itech.itech_backend.modules.product.dto.ProductVideoDto;
import com.itech.itech_backend.modules.product.model.Product;
import com.itech.itech_backend.modules.product.model.ProductVideo;
import com.itech.itech_backend.modules.product.repository.ProductRepository;
import com.itech.itech_backend.modules.product.repository.ProductVideoRepository;
import com.itech.itech_backend.modules.shared.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVideoService {

    private final ProductVideoRepository productVideoRepository;
    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;

    @Value("${app.upload.video-dir:uploads/videos}")
    private String videoUploadDir;

    @Value("${app.upload.thumbnail-dir:uploads/thumbnails}")
    private String thumbnailUploadDir;

    @Value("${app.upload.max-video-size:104857600}") // 100MB default
    private long maxVideoSize;

    // Create video from file upload
    @Transactional
    public ProductVideoDto uploadVideo(Long productId, MultipartFile videoFile, MultipartFile thumbnailFile,
                                     CreateProductVideoDto videoDto, String uploadedBy) {
        try {
            log.info("🎬 Uploading video for product {}: {}", productId, videoFile.getOriginalFilename());

            // Validate product exists
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

            // Validate file size
            if (videoFile.getSize() > maxVideoSize) {
                throw new RuntimeException("Video file size exceeds maximum allowed size: " + maxVideoSize + " bytes");
            }

            // Validate file type
            String contentType = videoFile.getContentType();
            if (!isValidVideoType(contentType)) {
                throw new RuntimeException("Invalid video file type: " + contentType);
            }

            // Upload video file
            String videoUrl = fileUploadService.uploadFile(videoFile, videoUploadDir);
            log.info("✅ Video uploaded successfully: {}", videoUrl);

            // Upload thumbnail if provided
            String thumbnailUrl = null;
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                thumbnailUrl = fileUploadService.uploadFile(thumbnailFile, thumbnailUploadDir);
                log.info("✅ Thumbnail uploaded successfully: {}", thumbnailUrl);
            }

            // Create video entity
            ProductVideo productVideo = ProductVideo.builder()
                    .product(product)
                    .title(videoDto.getTitle())
                    .description(videoDto.getDescription())
                    .videoUrl(videoUrl)
                    .thumbnailUrl(thumbnailUrl)
                    .fileName(videoFile.getOriginalFilename())
                    .fileSize(videoFile.getSize())
                    .format(getFileExtension(videoFile.getOriginalFilename()))
                    .videoType(videoDto.getVideoTypeEnum())
                    .status(ProductVideo.VideoStatus.PROCESSING)
                    .isPrimary(videoDto.getIsPrimary())
                    .sortOrder(videoDto.getSortOrder())
                    .processingStatus(ProductVideo.ProcessingStatus.PENDING)
                    .uploadedBy(uploadedBy)
                    .build();

            // Handle primary video logic
            if (videoDto.getIsPrimary() != null && videoDto.getIsPrimary()) {
                productVideoRepository.unsetAllPrimaryForProduct(productId);
            }

            ProductVideo savedVideo = productVideoRepository.save(productVideo);
            log.info("✅ Video record created with ID: {}", savedVideo.getId());

            // TODO: Queue for video processing (thumbnail generation, compression, etc.)
            // processVideoAsync(savedVideo.getId());

            return ProductVideoDto.fromEntity(savedVideo);

        } catch (Exception e) {
            log.error("❌ Error uploading video for product {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to upload video: " + e.getMessage());
        }
    }

    // Create video from external URL (YouTube, Vimeo, etc.)
    @Transactional
    public ProductVideoDto addExternalVideo(CreateProductVideoDto videoDto, String uploadedBy) {
        try {
            log.info("🌐 Adding external video for product {}: {}", videoDto.getProductId(), videoDto.getVideoUrl());

            // Validate product exists
            Product product = productRepository.findById(videoDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + videoDto.getProductId()));

            // Create video entity
            ProductVideo productVideo = ProductVideo.builder()
                    .product(product)
                    .title(videoDto.getTitle())
                    .description(videoDto.getDescription())
                    .videoUrl(videoDto.getVideoUrl())
                    .thumbnailUrl(videoDto.getThumbnailUrl())
                    .externalVideoId(videoDto.getExternalVideoId())
                    .externalPlatform(videoDto.getExternalPlatform())
                    .videoType(videoDto.getVideoTypeEnum())
                    .status(ProductVideo.VideoStatus.ACTIVE)
                    .isPrimary(videoDto.getIsPrimary())
                    .sortOrder(videoDto.getSortOrder())
                    .processingStatus(ProductVideo.ProcessingStatus.COMPLETED)
                    .uploadedBy(uploadedBy)
                    .build();

            // Handle primary video logic
            if (videoDto.getIsPrimary() != null && videoDto.getIsPrimary()) {
                productVideoRepository.unsetAllPrimaryForProduct(videoDto.getProductId());
            }

            ProductVideo savedVideo = productVideoRepository.save(productVideo);
            log.info("✅ External video added with ID: {}", savedVideo.getId());

            return ProductVideoDto.fromEntity(savedVideo);

        } catch (Exception e) {
            log.error("❌ Error adding external video: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add external video: " + e.getMessage());
        }
    }

    // Get videos by product
    public List<ProductVideoDto> getVideosByProduct(Long productId, ProductVideo.VideoStatus status) {
        try {
            log.debug("📹 Fetching videos for product {} with status {}", productId, status);
            
            List<ProductVideo> videos;
            if (status != null) {
                videos = productVideoRepository.findByProductIdAndStatus(productId, status);
            } else {
                videos = productVideoRepository.findByProductId(productId);
            }

            return videos.stream()
                    .map(ProductVideoDto::fromEntity)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("❌ Error fetching videos for product {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch videos: " + e.getMessage());
        }
    }

    // Get paginated videos by product
    public Page<ProductVideoDto> getVideosByProductPaginated(Long productId, ProductVideo.VideoStatus status, Pageable pageable) {
        try {
            Page<ProductVideo> videosPage = productVideoRepository.findByProductIdAndStatus(productId, status, pageable);
            return videosPage.map(ProductVideoDto::fromEntity);
        } catch (Exception e) {
            log.error("❌ Error fetching paginated videos for product {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch paginated videos: " + e.getMessage());
        }
    }

    // Get video by ID
    public ProductVideoDto getVideoById(Long videoId) {
        try {
            ProductVideo video = productVideoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found with ID: " + videoId));
            
            return ProductVideoDto.fromEntity(video);
        } catch (Exception e) {
            log.error("❌ Error fetching video {}: {}", videoId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch video: " + e.getMessage());
        }
    }

    // Update video
    @Transactional
    public ProductVideoDto updateVideo(Long videoId, CreateProductVideoDto updateDto, String updatedBy) {
        try {
            log.info("🔄 Updating video {}", videoId);

            ProductVideo video = productVideoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found with ID: " + videoId));

            // Update fields
            if (updateDto.getTitle() != null) {
                video.setTitle(updateDto.getTitle());
            }
            if (updateDto.getDescription() != null) {
                video.setDescription(updateDto.getDescription());
            }
            if (updateDto.getVideoType() != null) {
                video.setVideoType(updateDto.getVideoTypeEnum());
            }
            if (updateDto.getSortOrder() != null) {
                video.setSortOrder(updateDto.getSortOrder());
            }

            // Handle primary video logic
            if (updateDto.getIsPrimary() != null && updateDto.getIsPrimary() && !video.getIsPrimary()) {
                productVideoRepository.unsetAllPrimaryForProduct(video.getProduct().getId());
                video.setIsPrimary(true);
            }

            ProductVideo savedVideo = productVideoRepository.save(video);
            log.info("✅ Video updated successfully: {}", videoId);

            return ProductVideoDto.fromEntity(savedVideo);

        } catch (Exception e) {
            log.error("❌ Error updating video {}: {}", videoId, e.getMessage(), e);
            throw new RuntimeException("Failed to update video: " + e.getMessage());
        }
    }

    // Set primary video
    @Transactional
    public void setPrimaryVideo(Long videoId) {
        try {
            log.info("⭐ Setting primary video: {}", videoId);

            ProductVideo video = productVideoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found with ID: " + videoId));

            productVideoRepository.unsetAllPrimaryForProduct(video.getProduct().getId());
            productVideoRepository.setPrimaryVideo(videoId);

            log.info("✅ Primary video set successfully: {}", videoId);

        } catch (Exception e) {
            log.error("❌ Error setting primary video {}: {}", videoId, e.getMessage(), e);
            throw new RuntimeException("Failed to set primary video: " + e.getMessage());
        }
    }

    // Delete video (soft delete)
    @Transactional
    public void deleteVideo(Long videoId) {
        try {
            log.info("🗑️ Deleting video: {}", videoId);

            ProductVideo video = productVideoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found with ID: " + videoId));

            video.setStatus(ProductVideo.VideoStatus.DELETED);
            productVideoRepository.save(video);

            log.info("✅ Video deleted successfully: {}", videoId);

        } catch (Exception e) {
            log.error("❌ Error deleting video {}: {}", videoId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete video: " + e.getMessage());
        }
    }

    // Update sort orders
    @Transactional
    public void updateSortOrders(List<Long> videoIds) {
        try {
            log.info("📊 Updating sort orders for {} videos", videoIds.size());

            for (int i = 0; i < videoIds.size(); i++) {
                productVideoRepository.updateSortOrder(videoIds.get(i), i);
            }

            log.info("✅ Sort orders updated successfully");

        } catch (Exception e) {
            log.error("❌ Error updating sort orders: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update sort orders: " + e.getMessage());
        }
    }

    // Increment view count
    @Transactional
    public void incrementViewCount(Long videoId) {
        try {
            productVideoRepository.incrementViewCount(videoId);
        } catch (Exception e) {
            log.warn("⚠️ Failed to increment view count for video {}: {}", videoId, e.getMessage());
        }
    }

    // Get video statistics
    public ProductVideoStatsDto getVideoStatistics(Long productId) {
        try {
            long totalVideos = productVideoRepository.countByProductIdAndStatus(productId, ProductVideo.VideoStatus.ACTIVE);
            Long totalViews = productVideoRepository.getTotalViewsByProductId(productId, ProductVideo.VideoStatus.ACTIVE);
            
            return ProductVideoStatsDto.builder()
                    .totalVideos(totalVideos)
                    .totalViews(totalViews != null ? totalViews : 0L)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error getting video statistics for product {}: {}", productId, e.getMessage(), e);
            return ProductVideoStatsDto.builder()
                    .totalVideos(0L)
                    .totalViews(0L)
                    .build();
        }
    }

    // Helper methods
    private boolean isValidVideoType(String contentType) {
        if (contentType == null) return false;
        
        return contentType.startsWith("video/") && 
               (contentType.contains("mp4") || 
                contentType.contains("avi") || 
                contentType.contains("mov") || 
                contentType.contains("wmv") || 
                contentType.contains("webm") ||
                contentType.contains("mkv"));
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "unknown";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    // DTO for statistics
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ProductVideoStatsDto {
        private long totalVideos;
        private long totalViews;
    }
}

package com.itech.itech_backend.modules.product.controller;

import com.itech.itech_backend.modules.product.dto.CreateProductVideoDto;
import com.itech.itech_backend.modules.product.dto.ProductVideoDto;
import com.itech.itech_backend.modules.product.model.ProductVideo;
import com.itech.itech_backend.modules.product.service.ProductVideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/videos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class ProductVideoController {

    private final ProductVideoService productVideoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(
            @RequestParam("productId") Long productId,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "videoType", defaultValue = "PRODUCT_DEMO") String videoType,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary,
            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder,
            Authentication authentication) {
        
        try {
            log.info("🎬 Video upload request for product {} from user {}", productId, authentication.getName());

            // Validate file
            if (videoFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Video file is required",
                    "message", "Please select a video file to upload"
                ));
            }

            // Create DTO
            CreateProductVideoDto videoDto = CreateProductVideoDto.builder()
                    .productId(productId)
                    .title(title)
                    .description(description)
                    .videoType(videoType)
                    .isPrimary(isPrimary)
                    .sortOrder(sortOrder)
                    .build();

            // Upload video
            ProductVideoDto result = productVideoService.uploadVideo(
                    productId, videoFile, thumbnailFile, videoDto, authentication.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Video uploaded successfully",
                "video", result
            ));

        } catch (Exception e) {
            log.error("❌ Error uploading video for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Upload failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/external")
    public ResponseEntity<?> addExternalVideo(
            @Valid @RequestBody CreateProductVideoDto videoDto,
            Authentication authentication) {
        
        try {
            log.info("🌐 External video request for product {} from user {}", videoDto.getProductId(), authentication.getName());

            ProductVideoDto result = productVideoService.addExternalVideo(videoDto, authentication.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "External video added successfully",
                "video", result
            ));

        } catch (Exception e) {
            log.error("❌ Error adding external video: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to add external video",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getVideosByProduct(
            @PathVariable Long productId,
            @RequestParam(required = false) String status) {
        
        try {
            log.debug("📹 Fetching videos for product {} with status {}", productId, status);

            ProductVideo.VideoStatus videoStatus = null;
            if (status != null) {
                try {
                    videoStatus = ProductVideo.VideoStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid status",
                        "message", "Valid statuses: ACTIVE, INACTIVE, PROCESSING, FAILED, DELETED"
                    ));
                }
            }

            List<ProductVideoDto> videos = productVideoService.getVideosByProduct(productId, videoStatus);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "videos", videos,
                "count", videos.size()
            ));

        } catch (Exception e) {
            log.error("❌ Error fetching videos for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to fetch videos",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/product/{productId}/paginated")
    public ResponseEntity<?> getVideosByProductPaginated(
            @PathVariable Long productId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            ProductVideo.VideoStatus videoStatus = status != null ? 
                ProductVideo.VideoStatus.valueOf(status.toUpperCase()) : 
                ProductVideo.VideoStatus.ACTIVE;

            Pageable pageable = PageRequest.of(page, size);
            Page<ProductVideoDto> videosPage = productVideoService.getVideosByProductPaginated(productId, videoStatus, pageable);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "videos", videosPage.getContent(),
                "totalElements", videosPage.getTotalElements(),
                "totalPages", videosPage.getTotalPages(),
                "currentPage", videosPage.getNumber(),
                "size", videosPage.getSize(),
                "hasNext", videosPage.hasNext(),
                "hasPrevious", videosPage.hasPrevious()
            ));

        } catch (Exception e) {
            log.error("❌ Error fetching paginated videos for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to fetch paginated videos",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<?> getVideoById(@PathVariable Long videoId) {
        try {
            ProductVideoDto video = productVideoService.getVideoById(videoId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "video", video
            ));

        } catch (Exception e) {
            log.error("❌ Error fetching video {}: {}", videoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Video not found",
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{videoId}")
    public ResponseEntity<?> updateVideo(
            @PathVariable Long videoId,
            @Valid @RequestBody CreateProductVideoDto updateDto,
            Authentication authentication) {
        
        try {
            log.info("🔄 Updating video {} by user {}", videoId, authentication.getName());

            ProductVideoDto result = productVideoService.updateVideo(videoId, updateDto, authentication.getName());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Video updated successfully",
                "video", result
            ));

        } catch (Exception e) {
            log.error("❌ Error updating video {}: {}", videoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to update video",
                "message", e.getMessage()
            ));
        }
    }

    @PatchMapping("/{videoId}/primary")
    public ResponseEntity<?> setPrimaryVideo(@PathVariable Long videoId) {
        try {
            log.info("⭐ Setting primary video: {}", videoId);

            productVideoService.setPrimaryVideo(videoId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Primary video set successfully"
            ));

        } catch (Exception e) {
            log.error("❌ Error setting primary video {}: {}", videoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to set primary video",
                "message", e.getMessage()
            ));
        }
    }

    @PatchMapping("/sort-order")
    public ResponseEntity<?> updateSortOrders(@RequestBody List<Long> videoIds) {
        try {
            log.info("📊 Updating sort orders for {} videos", videoIds.size());

            productVideoService.updateSortOrders(videoIds);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sort orders updated successfully"
            ));

        } catch (Exception e) {
            log.error("❌ Error updating sort orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to update sort orders",
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long videoId) {
        try {
            log.info("🗑️ Deleting video: {}", videoId);

            productVideoService.deleteVideo(videoId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Video deleted successfully"
            ));

        } catch (Exception e) {
            log.error("❌ Error deleting video {}: {}", videoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to delete video",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{videoId}/view")
    public ResponseEntity<?> incrementViewCount(@PathVariable Long videoId) {
        try {
            productVideoService.incrementViewCount(videoId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "View count incremented"
            ));

        } catch (Exception e) {
            log.warn("⚠️ Failed to increment view count for video {}: {}", videoId, e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Failed to increment view count"
            ));
        }
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<?> getVideoStatistics(@PathVariable Long productId) {
        try {
            ProductVideoService.ProductVideoStatsDto stats = productVideoService.getVideoStatistics(productId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", stats
            ));

        } catch (Exception e) {
            log.error("❌ Error getting video statistics for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to get video statistics",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/types")
    public ResponseEntity<?> getVideoTypes() {
        try {
            ProductVideo.VideoType[] videoTypes = ProductVideo.VideoType.values();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "videoTypes", videoTypes
            ));
        } catch (Exception e) {
            log.error("❌ Error getting video types: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to get video types",
                "message", e.getMessage()
            ));
        }
    }
}

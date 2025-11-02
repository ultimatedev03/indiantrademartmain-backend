package com.itech.itech_backend.modules.product.repository;

import com.itech.itech_backend.modules.product.model.ProductVideo;
import com.itech.itech_backend.modules.product.model.ProductVideo.VideoStatus;
import com.itech.itech_backend.modules.product.model.ProductVideo.VideoType;
import com.itech.itech_backend.modules.product.model.ProductVideo.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVideoRepository extends JpaRepository<ProductVideo, Long> {

    // Find videos by product
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.id = :productId AND pv.status = :status ORDER BY pv.isPrimary DESC, pv.sortOrder ASC, pv.createdAt DESC")
    List<ProductVideo> findByProductIdAndStatus(@Param("productId") Long productId, @Param("status") VideoStatus status);

    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.id = :productId ORDER BY pv.isPrimary DESC, pv.sortOrder ASC, pv.createdAt DESC")
    List<ProductVideo> findByProductId(@Param("productId") Long productId);

    // Find paginated videos by product
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.id = :productId AND pv.status = :status ORDER BY pv.isPrimary DESC, pv.sortOrder ASC, pv.createdAt DESC")
    Page<ProductVideo> findByProductIdAndStatus(@Param("productId") Long productId, @Param("status") VideoStatus status, Pageable pageable);

    // Find active videos
    List<ProductVideo> findByProductIdAndStatusOrderByIsPrimaryDescSortOrderAscCreatedAtDesc(Long productId, VideoStatus status);

    // Find by video type
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.id = :productId AND pv.videoType = :videoType AND pv.status = :status ORDER BY pv.sortOrder ASC")
    List<ProductVideo> findByProductIdAndVideoTypeAndStatus(@Param("productId") Long productId, @Param("videoType") VideoType videoType, @Param("status") VideoStatus status);

    // Find primary video
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.id = :productId AND pv.isPrimary = true AND pv.status = :status")
    Optional<ProductVideo> findPrimaryVideoByProductId(@Param("productId") Long productId, @Param("status") VideoStatus status);

    // Count videos by product
    long countByProductIdAndStatus(Long productId, VideoStatus status);

    // Find videos by processing status
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.processingStatus = :processingStatus ORDER BY pv.createdAt ASC")
    List<ProductVideo> findByProcessingStatus(@Param("processingStatus") ProcessingStatus processingStatus);

    // Find videos for processing (pending or failed)
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.processingStatus IN ('PENDING', 'FAILED') ORDER BY pv.createdAt ASC")
    List<ProductVideo> findVideosForProcessing();

    // Find videos by vendor (through product)
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.vendor.id = :vendorId AND pv.status = :status ORDER BY pv.createdAt DESC")
    List<ProductVideo> findByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") VideoStatus status);

    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.vendor.id = :vendorId AND pv.status = :status ORDER BY pv.createdAt DESC")
    Page<ProductVideo> findByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") VideoStatus status, Pageable pageable);

    // Search videos by title or description
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.product.id = :productId AND pv.status = :status AND " +
           "(LOWER(pv.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pv.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY pv.isPrimary DESC, pv.sortOrder ASC")
    List<ProductVideo> searchByProductIdAndTerm(@Param("productId") Long productId, @Param("status") VideoStatus status, @Param("searchTerm") String searchTerm);

    // Update sort orders
    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.sortOrder = :sortOrder WHERE pv.id = :videoId")
    void updateSortOrder(@Param("videoId") Long videoId, @Param("sortOrder") Integer sortOrder);

    // Set primary video (first unset all, then set the new one)
    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.isPrimary = false WHERE pv.product.id = :productId")
    void unsetAllPrimaryForProduct(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.isPrimary = true WHERE pv.id = :videoId")
    void setPrimaryVideo(@Param("videoId") Long videoId);

    // Update processing status
    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.processingStatus = :status, pv.processingError = :error WHERE pv.id = :videoId")
    void updateProcessingStatus(@Param("videoId") Long videoId, @Param("status") ProcessingStatus status, @Param("error") String error);

    // Update video metadata after processing
    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.duration = :duration, pv.width = :width, pv.height = :height, " +
           "pv.format = :format, pv.fileSize = :fileSize, pv.thumbnailUrl = :thumbnailUrl, " +
           "pv.processingStatus = :status WHERE pv.id = :videoId")
    void updateVideoMetadata(@Param("videoId") Long videoId, @Param("duration") Integer duration, 
                           @Param("width") Integer width, @Param("height") Integer height,
                           @Param("format") String format, @Param("fileSize") Long fileSize,
                           @Param("thumbnailUrl") String thumbnailUrl, @Param("status") ProcessingStatus status);

    // Update view count
    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.viewCount = pv.viewCount + 1 WHERE pv.id = :videoId")
    void incrementViewCount(@Param("videoId") Long videoId);

    // Update download count
    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.downloadCount = pv.downloadCount + 1 WHERE pv.id = :videoId")
    void incrementDownloadCount(@Param("videoId") Long videoId);

    // Delete videos by product (soft delete by setting status to DELETED)
    @Modifying
    @Query("UPDATE ProductVideo pv SET pv.status = 'DELETED' WHERE pv.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    // Find videos created within date range
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.createdAt BETWEEN :startDate AND :endDate AND pv.status = :status ORDER BY pv.createdAt DESC")
    List<ProductVideo> findByCreatedAtBetweenAndStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("status") VideoStatus status);

    // Get video statistics
    @Query("SELECT COUNT(pv) FROM ProductVideo pv WHERE pv.status = :status")
    long countByStatus(@Param("status") VideoStatus status);

    @Query("SELECT SUM(pv.viewCount) FROM ProductVideo pv WHERE pv.product.id = :productId AND pv.status = :status")
    Long getTotalViewsByProductId(@Param("productId") Long productId, @Param("status") VideoStatus status);

    @Query("SELECT SUM(pv.fileSize) FROM ProductVideo pv WHERE pv.product.vendor.id = :vendorId AND pv.status = :status")
    Long getTotalFileSizeByVendorId(@Param("vendorId") Long vendorId, @Param("status") VideoStatus status);

    // Find most viewed videos
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.status = :status ORDER BY pv.viewCount DESC")
    Page<ProductVideo> findMostViewedVideos(@Param("status") VideoStatus status, Pageable pageable);

    // Find videos by external platform (YouTube, Vimeo, etc.)
    @Query("SELECT pv FROM ProductVideo pv WHERE pv.externalPlatform = :platform AND pv.status = :status ORDER BY pv.createdAt DESC")
    List<ProductVideo> findByExternalPlatformAndStatus(@Param("platform") String platform, @Param("status") VideoStatus status);

    // Custom query to find videos with specific criteria
    @Query("SELECT pv FROM ProductVideo pv WHERE " +
           "(:productId IS NULL OR pv.product.id = :productId) AND " +
           "(:vendorId IS NULL OR pv.product.vendor.id = :vendorId) AND " +
           "(:videoType IS NULL OR pv.videoType = :videoType) AND " +
           "(:status IS NULL OR pv.status = :status) " +
           "ORDER BY pv.isPrimary DESC, pv.sortOrder ASC, pv.createdAt DESC")
    Page<ProductVideo> findVideosByCriteria(@Param("productId") Long productId, @Param("vendorId") Long vendorId,
                                          @Param("videoType") VideoType videoType, @Param("status") VideoStatus status,
                                          Pageable pageable);
}

package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.modules.vendor.model.VendorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {

    // Basic finders
    Optional<VendorProfile> findByVendorName(String vendorName);
    
    Optional<VendorProfile> findByCompanyId(Long companyId);
    
    Optional<VendorProfile> findByUserId(Long userId);

    // Status-based queries
    List<VendorProfile> findByVendorStatus(VendorProfile.VendorStatus status);
    
    Page<VendorProfile> findByVendorStatusAndIsActive(
        VendorProfile.VendorStatus status, 
        Boolean isActive, 
        Pageable pageable
    );

    // Verification queries
    List<VendorProfile> findByKycApprovedTrue();
    
    List<VendorProfile> findByKycSubmittedTrueAndKycApprovedFalse();

    // Performance-based queries
    @Query("SELECT v FROM VendorProfile v WHERE v.averageRating >= :minRating ORDER BY v.averageRating DESC")
    Page<VendorProfile> findByMinimumRating(@Param("minRating") BigDecimal minRating, Pageable pageable);

    @Query("SELECT v FROM VendorProfile v WHERE v.totalOrders > 0 ORDER BY v.totalOrders DESC")
    Page<VendorProfile> findActiveVendors(Pageable pageable);

    // Featured vendors
    List<VendorProfile> findByFeaturedVendorTrue();
    
    Page<VendorProfile> findByFeaturedVendorTrueAndVendorStatusAndIsActive(
        VendorProfile.VendorStatus status, 
        Boolean isActive, 
        Pageable pageable
    );

    // Search queries
    @Query("SELECT v FROM VendorProfile v WHERE " +
           "LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.displayName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.businessType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<VendorProfile> searchVendors(@Param("keyword") String keyword, Pageable pageable);

    // Company-based queries
    @Query("SELECT v FROM VendorProfile v JOIN v.company c WHERE " +
           "c.city = :city AND v.vendorStatus = :status AND v.isActive = true")
    Page<VendorProfile> findByCityAndStatus(
        @Param("city") String city, 
        @Param("status") VendorProfile.VendorStatus status, 
        Pageable pageable
    );

    @Query("SELECT v FROM VendorProfile v JOIN v.company c WHERE " +
           "c.state = :state AND v.vendorStatus = :status AND v.isActive = true")
    Page<VendorProfile> findByStateAndStatus(
        @Param("state") String state, 
        @Param("status") VendorProfile.VendorStatus status, 
        Pageable pageable
    );

    // Analytics queries
    @Query("SELECT COUNT(v) FROM VendorProfile v WHERE v.vendorStatus = :status")
    Long countByStatus(@Param("status") VendorProfile.VendorStatus status);

    @Query("SELECT COUNT(v) FROM VendorProfile v WHERE v.kycApproved = true AND v.isActive = true")
    Long countVerifiedVendors();

    @Query("SELECT AVG(v.averageRating) FROM VendorProfile v WHERE v.totalReviews > 0")
    Optional<BigDecimal> getAverageRating();

    // Top performers
    @Query("SELECT v FROM VendorProfile v WHERE v.vendorStatus = :status AND v.isActive = true " +
           "ORDER BY v.totalRevenue DESC")
    Page<VendorProfile> findTopVendorsByRevenue(
        @Param("status") VendorProfile.VendorStatus status, 
        Pageable pageable
    );

    @Query("SELECT v FROM VendorProfile v WHERE v.vendorStatus = :status AND v.isActive = true " +
           "ORDER BY v.averageRating DESC, v.totalReviews DESC")
    Page<VendorProfile> findTopRatedVendors(
        @Param("status") VendorProfile.VendorStatus status, 
        Pageable pageable
    );

    // Custom update queries
    @Query("UPDATE VendorProfile v SET v.profileViews = v.profileViews + 1 WHERE v.id = :vendorId")
    int incrementProfileViews(@Param("vendorId") Long vendorId);

    @Query("UPDATE VendorProfile v SET v.inquiryCount = v.inquiryCount + 1 WHERE v.id = :vendorId")
    int incrementInquiryCount(@Param("vendorId") Long vendorId);
}

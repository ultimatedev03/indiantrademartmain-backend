package com.itech.itech_backend.modules.vendor.repository;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    
    // Find by unique identifiers
    Optional<Vendor> findByEmail(String email);
    Optional<Vendor> findByPhone(String phone);
    Optional<Vendor> findByCompanyId(Long companyId);
    
    // Existence checks for unique fields
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByCompanyId(Long companyId);
    
    // Find by verification status  
    List<Vendor> findByVerificationStatus(com.itech.itech_backend.enums.VerificationStatus verificationStatus);
    Page<Vendor> findByVerificationStatus(com.itech.itech_backend.enums.VerificationStatus verificationStatus, Pageable pageable);
    
    // Find by business type
    List<Vendor> findByBusinessType(com.itech.itech_backend.enums.VendorBusinessType businessType);
    Page<Vendor> findByBusinessType(com.itech.itech_backend.enums.VendorBusinessType businessType, Pageable pageable);
    
    // Find by active status
    List<Vendor> findByIsActive(Boolean isActive);
    Page<Vendor> findByIsActive(Boolean isActive, Pageable pageable);
    
    // Find verified vendors
    List<Vendor> findByIsVerified(Boolean isVerified);
    Page<Vendor> findByIsVerified(Boolean isVerified, Pageable pageable);
    
    // Find by KYC status
    List<Vendor> findByKycSubmitted(Boolean kycSubmitted);
    List<Vendor> findByKycApproved(Boolean kycApproved);
    Page<Vendor> findByKycSubmitted(Boolean kycSubmitted, Pageable pageable);
    Page<Vendor> findByKycApproved(Boolean kycApproved, Pageable pageable);
    
    // Find pending KYC vendors
    @Query("SELECT v FROM Vendor v WHERE v.kycSubmitted = true AND v.kycApproved = false")
    Page<Vendor> findPendingKycVendors(Pageable pageable);
    
    // Find by business categories
    @Query("SELECT v FROM Vendor v JOIN v.categories c WHERE c = :category")
    List<Vendor> findByCategory(@Param("category") Vendor.BusinessCategory category);
    
    @Query("SELECT v FROM Vendor v JOIN v.categories c WHERE c = :category")
    Page<Vendor> findByCategory(@Param("category") Vendor.BusinessCategory category, Pageable pageable);
    
    // Find by service areas
    @Query("SELECT v FROM Vendor v JOIN v.serviceAreas sa WHERE LOWER(sa) = LOWER(:area)")
    List<Vendor> findByServiceArea(@Param("area") String area);
    
    @Query("SELECT v FROM Vendor v JOIN v.serviceAreas sa WHERE LOWER(sa) = LOWER(:area)")
    Page<Vendor> findByServiceArea(@Param("area") String area, Pageable pageable);
    
    // Find by features
    List<Vendor> findByDeliveryAvailable(Boolean deliveryAvailable);
    List<Vendor> findByInstallationService(Boolean installationService);
    List<Vendor> findByAfterSalesSupport(Boolean afterSalesSupport);
    List<Vendor> findByFeaturedVendor(Boolean featuredVendor);
    List<Vendor> findByPriorityListing(Boolean priorityListing);
    
    // Search functionality
    @Query("SELECT v FROM Vendor v WHERE " +
           "LOWER(v.businessName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CAST(v.businessType AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Vendor> searchVendors(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Advanced search with filters
    @Query("SELECT v FROM Vendor v WHERE " +
           "(:vendorName IS NULL OR LOWER(v.businessName) LIKE LOWER(CONCAT('%', :vendorName, '%'))) AND " +
           "(:vendorType IS NULL OR v.businessType = :vendorType) AND " +
           "(:vendorStatus IS NULL OR v.verificationStatus = :vendorStatus) AND " +
           "(:isActive IS NULL OR v.isActive = :isActive) AND " +
           "(:isVerified IS NULL OR v.isVerified = :isVerified) AND " +
           "(:kycApproved IS NULL OR v.kycApproved = :kycApproved) AND " +
           "(:minRating IS NULL OR v.averageRating >= :minRating) AND " +
           "(:deliveryAvailable IS NULL OR v.deliveryAvailable = :deliveryAvailable) AND " +
           "(:installationService IS NULL OR v.installationService = :installationService)")
    Page<Vendor> findVendorsWithFilters(
        @Param("vendorName") String vendorName,
        @Param("vendorType") com.itech.itech_backend.enums.VendorBusinessType vendorType,
        @Param("vendorStatus") com.itech.itech_backend.enums.VerificationStatus vendorStatus,
        @Param("isActive") Boolean isActive,
        @Param("isVerified") Boolean isVerified,
        @Param("kycApproved") Boolean kycApproved,
        @Param("minRating") BigDecimal minRating,
        @Param("deliveryAvailable") Boolean deliveryAvailable,
        @Param("installationService") Boolean installationService,
        Pageable pageable
    );
    
    // Performance-based queries
    @Query("SELECT v FROM Vendor v WHERE v.averageRating >= :minRating ORDER BY v.averageRating DESC")
    Page<Vendor> findByMinimumRating(@Param("minRating") BigDecimal minRating, Pageable pageable);
    
    @Query("SELECT v FROM Vendor v WHERE v.totalOrders >= :minOrders ORDER BY v.totalOrders DESC")
    Page<Vendor> findByMinimumOrders(@Param("minOrders") Long minOrders, Pageable pageable);
    
    @Query("SELECT v FROM Vendor v WHERE v.totalRevenue >= :minRevenue ORDER BY v.totalRevenue DESC")
    Page<Vendor> findByMinimumRevenue(@Param("minRevenue") BigDecimal minRevenue, Pageable pageable);
    
    // Top performing vendors
    @Query("SELECT v FROM Vendor v WHERE v.isActive = true AND v.isVerified = true " +
           "ORDER BY v.averageRating DESC, v.totalOrders DESC")
    Page<Vendor> findTopPerformingVendors(Pageable pageable);
    
    // Featured and premium vendors
    @Query("SELECT v FROM Vendor v WHERE v.featuredVendor = true AND v.isActive = true " +
           "ORDER BY v.businessType DESC, v.averageRating DESC")
    Page<Vendor> findFeaturedVendors(Pageable pageable);
    
    // Recent vendors
    @Query("SELECT v FROM Vendor v ORDER BY v.createdAt DESC")
    Page<Vendor> findRecentVendors(Pageable pageable);
    
    // Active vendors with subscriptions
    @Query("SELECT v FROM Vendor v WHERE v.subscriptionEndDate > :currentDate AND v.isActive = true")
    List<Vendor> findActiveSubscriptions(@Param("currentDate") LocalDateTime currentDate);
    
    // Expiring subscriptions
    @Query("SELECT v FROM Vendor v WHERE v.subscriptionEndDate BETWEEN :startDate AND :endDate")
    List<Vendor> findExpiringSubscriptions(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    // Statistics queries
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.verificationStatus = :status")
    long countByVerificationStatus(@Param("status") com.itech.itech_backend.enums.VerificationStatus status);
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.businessType = :type")
    long countByBusinessType(@Param("type") com.itech.itech_backend.enums.VendorBusinessType type);
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.isVerified = true")
    long countVerifiedVendors();
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.kycApproved = true")
    long countKycApprovedVendors();
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.isActive = true")
    long countActiveVendors();
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.featuredVendor = true")
    long countFeaturedVendors();
    
    // Analytics queries
    @Query("SELECT AVG(v.averageRating) FROM Vendor v WHERE v.totalReviews > 0")
    BigDecimal getAverageVendorRating();
    
    @Query("SELECT SUM(v.totalRevenue) FROM Vendor v")
    BigDecimal getTotalPlatformRevenue();
    
    @Query("SELECT SUM(v.totalOrders) FROM Vendor v")
    Long getTotalOrders();
    
    // Find vendors by subscription status
    @Query("SELECT v FROM Vendor v WHERE " +
           "v.subscriptionEndDate IS NOT NULL AND v.subscriptionEndDate > :currentDate")
    Page<Vendor> findActiveSubscriptionVendors(@Param("currentDate") LocalDateTime currentDate, Pageable pageable);
    
    // Find vendors by establishment year range
    List<Vendor> findByEstablishedYearBetween(Integer startYear, Integer endYear);
    
    // Find vendors by minimum order value range
    List<Vendor> findByMinimumOrderValueBetween(BigDecimal minValue, BigDecimal maxValue);
    
    // Find vendors by payment terms
    List<Vendor> findByPaymentTermsDaysLessThanEqual(Integer maxDays);
    
    // Find vendors with certifications
    @Query("SELECT v FROM Vendor v WHERE v.isoCertified = true OR v.qualityAssured = true")
    Page<Vendor> findCertifiedVendors(Pageable pageable);
    
    // Find inactive vendors for cleanup
    @Query("SELECT v FROM Vendor v WHERE v.lastActivity < :cutoffDate OR v.lastLogin < :cutoffDate")
    List<Vendor> findInactiveVendors(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Find vendors by response time
    List<Vendor> findByResponseTimeHoursLessThanEqual(Integer maxHours);
    
    // Dashboard queries
    @Query("SELECT v FROM Vendor v WHERE v.createdAt >= :fromDate ORDER BY v.createdAt DESC")
    List<Vendor> findVendorsRegisteredSince(@Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT v FROM Vendor v WHERE v.verificationStatus = 'PENDING' ORDER BY v.createdAt ASC")
    Page<Vendor> findPendingApprovalVendors(Pageable pageable);
}


package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.vendor.dto.*;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VendorService {
    
    // CRUD Operations
    VendorDto createVendor(CreateVendorDto createVendorDto);
    VendorDto getVendorById(Long vendorId);
    VendorDto updateVendor(Long vendorId, UpdateVendorDto updateVendorDto);
    void deleteVendor(Long vendorId);
    
    // Vendor listing and search
    Page<VendorDto> getAllVendors(Pageable pageable);
    Page<VendorDto> searchVendors(String searchTerm, Pageable pageable);
    Page<VendorDto> getVendorsWithFilters(String vendorName, com.itech.itech_backend.enums.VendorBusinessType businessType,
                                        com.itech.itech_backend.enums.VerificationStatus verificationStatus, Boolean isActive,
                                        Boolean isVerified, Boolean kycApproved,
                                        BigDecimal minRating, Boolean deliveryAvailable,
                                        Boolean installationService, Pageable pageable);
    
    // Vendor verification and KYC
    VendorDto verifyVendor(VendorVerificationDto verificationDto);
    VendorDto submitKyc(Long vendorId, List<String> documentUrls);
    Page<VendorDto> getPendingKycVendors(Pageable pageable);
    Page<VendorDto> getPendingApprovalVendors(Pageable pageable);
    Page<VendorDto> getVendorsByStatus(com.itech.itech_backend.enums.VerificationStatus status, Pageable pageable);
    
    // Vendor status management
    VendorDto updateVendorStatus(Long vendorId, com.itech.itech_backend.enums.VerificationStatus status);
    VendorDto activateVendor(Long vendorId);
    VendorDto deactivateVendor(Long vendorId);
    VendorDto suspendVendor(Long vendorId, String reason);
    
    // Vendor type and subscription management
    Page<VendorDto> getVendorsByType(com.itech.itech_backend.enums.VendorBusinessType businessType, Pageable pageable);
    VendorDto upgradeVendorType(Long vendorId, com.itech.itech_backend.enums.VendorBusinessType businessType);
    VendorDto setFeaturedVendor(Long vendorId, Boolean featured);
    VendorDto setPriorityListing(Long vendorId, Boolean priority);
    
    // Business category and specialization
    Page<VendorDto> getVendorsByCategory(Vendor.BusinessCategory category, Pageable pageable);
    Page<VendorDto> getVendorsByServiceArea(String area, Pageable pageable);
    List<VendorDto> getVendorsByFeature(String feature, Boolean value);
    
    // Performance and analytics
    Page<VendorDto> getTopPerformingVendors(Pageable pageable);
    Page<VendorDto> getVendorsByRating(BigDecimal minRating, Pageable pageable);
    Page<VendorDto> getVendorsByOrders(Long minOrders, Pageable pageable);
    Page<VendorDto> getVendorsByRevenue(BigDecimal minRevenue, Pageable pageable);
    Page<VendorDto> getFeaturedVendors(Pageable pageable);
    Page<VendorDto> getCertifiedVendors(Pageable pageable);
    
    // Vendor profile management
    VendorDto updateVendorProfile(Long vendorId, String displayName, String description);
    VendorDto updateProfileImages(Long vendorId, String profileImageUrl, String coverImageUrl);
    VendorDto updateContactPerson(Long vendorId, String name, String designation, String phone, String email);
    VendorDto updateBusinessInfo(Long vendorId, Integer establishedYear, String businessType, 
                                List<Vendor.BusinessCategory> categories, List<String> specializations);
    VendorDto updateServiceAreas(Long vendorId, List<String> serviceAreas);
    VendorDto updatePaymentMethods(Long vendorId, List<Vendor.PaymentMethod> paymentMethods);
    VendorDto updateCertifications(Long vendorId, List<String> certifications, Boolean isoCertified, Boolean qualityAssured);
    
    // Financial and business terms
    VendorDto updateFinancialInfo(Long vendorId, BigDecimal minimumOrderValue, BigDecimal creditLimit, Integer paymentTerms);
    VendorDto updateServiceCapabilities(Long vendorId, Boolean delivery, Boolean installation, Boolean afterSales);
    
    // Subscription and premium features
    Page<VendorDto> getActiveSubscriptionVendors(Pageable pageable);
    List<VendorDto> getExpiringSubscriptions(int daysAhead);
    VendorDto renewSubscription(Long vendorId, int months);
    VendorDto cancelSubscription(Long vendorId);
    
    // Document management
    VendorDto uploadDocument(Long vendorId, String documentUrl);
    VendorDto removeDocument(Long vendorId, String documentUrl);
    List<String> getVendorDocuments(Long vendorId);
    
    // Settings and preferences
    VendorDto updateNotificationSettings(Long vendorId, Boolean email, Boolean sms);
    VendorDto updateBusinessSettings(Long vendorId, Boolean autoApprove, Vendor.CatalogVisibility visibility);
    VendorDto updateMarketingContent(Long vendorId, String bannerUrl, String videoUrl, String socialMediaLinks);
    
    // Authentication and validation
    boolean isEmailUnique(String email);
    boolean isPhoneUnique(String phone);
    boolean isCompanyLinked(Long companyId);
    VendorDto getVendorByEmail(String email);
    VendorDto getVendorByPhone(String phone);
    VendorDto getVendorByCompany(Long companyId);
    VendorDto authenticateVendor(String email, String password);
    VendorDto updatePassword(Long vendorId, String currentPassword, String newPassword);
    
    // Activity tracking
    VendorDto updateLastLogin(Long vendorId);
    VendorDto updateLastActivity(Long vendorId);
    VendorDto incrementProfileViews(Long vendorId);
    VendorDto incrementProductViews(Long vendorId, Long views);
    VendorDto incrementInquiryCount(Long vendorId);
    
    // Performance metrics updates
    VendorDto updateOrderMetrics(Long vendorId, Long totalOrders, Long completedOrders, Long cancelledOrders);
    VendorDto updateRevenueMetrics(Long vendorId, BigDecimal revenue);
    VendorDto updateRatingMetrics(Long vendorId, BigDecimal rating, Integer totalReviews);
    VendorDto updateResponseTime(Long vendorId, Integer hours);
    VendorDto updateFulfillmentTime(Long vendorId, Integer days);
    
    // Statistics and analytics
    Map<String, Long> getVendorStatistics();
    long getTotalVendorsCount();
    long getActiveVendorsCount();
    long getVerifiedVendorsCount();
    long getKycApprovedVendorsCount();
    long getFeaturedVendorsCount();
    Map<com.itech.itech_backend.enums.VerificationStatus, Long> getVendorCountByStatus();
    Map<com.itech.itech_backend.enums.VendorBusinessType, Long> getVendorCountByType();
    Map<Vendor.BusinessCategory, Long> getVendorCountByCategory();
    BigDecimal getAverageVendorRating();
    BigDecimal getTotalPlatformRevenue();
    Long getTotalOrders();
    
    // Recent and trending
    Page<VendorDto> getRecentVendors(Pageable pageable);
    List<VendorDto> getNewRegistrations(int days);
    List<VendorDto> getInactiveVendors(int days);
    
    // Bulk operations
    List<VendorDto> createVendorsInBulk(List<CreateVendorDto> createVendorDtos);
    Map<String, Object> bulkUpdateVendorStatus(List<Long> vendorIds, com.itech.itech_backend.enums.VerificationStatus status);
    Map<String, Object> bulkVerifyVendors(List<Long> vendorIds, Boolean approved);
    Map<String, Object> bulkUpdateVendorType(List<Long> vendorIds, com.itech.itech_backend.enums.VendorBusinessType businessType);
    
    // Export functionality
    byte[] exportVendorsToExcel(List<Long> vendorIds);
    byte[] exportVendorsToPdf(List<Long> vendorIds);
    
    // Integration with other modules
    VendorDto linkCompany(Long vendorId, Long companyId);
    VendorDto unlinkCompany(Long vendorId);
    
    // Dashboard and reporting
    Map<String, Object> getVendorDashboardData(Long vendorId);
    Map<String, Object> getAdminDashboardData();
    List<VendorDto> getVendorRecommendations(String area, Vendor.BusinessCategory category, int limit);
}


package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.enums.KycStatus;
import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.modules.buyer.dto.BuyerDto;
import com.itech.itech_backend.modules.buyer.dto.BuyerVerificationDto;
import com.itech.itech_backend.modules.buyer.dto.CreateBuyerDto;
import com.itech.itech_backend.modules.buyer.dto.UpdateBuyerDto;
import com.itech.itech_backend.modules.buyer.model.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BuyerService {

    // ===============================
    // CORE CRUD OPERATIONS
    // ===============================
    
    /**
     * Create a new buyer with validation and password encryption
     */
    BuyerDto createBuyer(CreateBuyerDto createBuyerDto);
    
    /**
     * Get buyer by ID
     */
    Optional<BuyerDto> getBuyerById(Long buyerId);
    
    /**
     * Get buyer by email
     */
    Optional<BuyerDto> getBuyerByEmail(String email);
    
    /**
     * Get buyer by phone
     */
    Optional<BuyerDto> getBuyerByPhone(String phone);
    
    /**
     * Update buyer profile information
     */
    BuyerDto updateBuyer(Long buyerId, UpdateBuyerDto updateBuyerDto);
    
    /**
     * Delete buyer (soft delete)
     */
    void deleteBuyer(Long buyerId);
    
    /**
     * Hard delete buyer (permanent removal)
     */
    void hardDeleteBuyer(Long buyerId);

    // ===============================
    // AUTHENTICATION & SECURITY
    // ===============================
    
    /**
     * Authenticate buyer login
     */
    Optional<BuyerDto> authenticateBuyer(String email, String password);
    
    /**
     * Update buyer password
     */
    void updatePassword(Long buyerId, String currentPassword, String newPassword);
    
    /**
     * Reset password with token
     */
    void resetPassword(String email, String resetToken, String newPassword);
    
    /**
     * Generate password reset token
     */
    String generatePasswordResetToken(String email);
    
    /**
     * Enable/disable two-factor authentication
     */
    void updateTwoFactorAuthentication(Long buyerId, Boolean enabled);
    
    /**
     * Update buyer login timestamp
     */
    void updateLastLogin(Long buyerId);

    // ===============================
    // VERIFICATION OPERATIONS
    // ===============================
    
    /**
     * Send email verification
     */
    void sendEmailVerification(Long buyerId);
    
    /**
     * Verify email with token
     */
    boolean verifyEmail(Long buyerId, String verificationToken);
    
    /**
     * Send phone verification OTP
     */
    void sendPhoneVerification(Long buyerId);
    
    /**
     * Verify phone with OTP
     */
    boolean verifyPhone(Long buyerId, String otp);
    
    /**
     * Start KYC verification process
     */
    void initiateKycVerification(Long buyerId, Map<String, Object> kycData);
    
    /**
     * Update KYC status
     */
    void updateKycStatus(Long buyerId, KycStatus kycStatus, String reason);
    
    /**
     * Get buyer verification details
     */
    BuyerVerificationDto getBuyerVerification(Long buyerId);
    
    /**
     * Upload and verify documents
     */
    void uploadVerificationDocument(Long buyerId, String documentType, 
                                    String documentNumber, String documentUrl);

    // ===============================
    // STATUS MANAGEMENT
    // ===============================
    
    /**
     * Activate buyer account
     */
    void activateBuyer(Long buyerId, String reason);
    
    /**
     * Deactivate buyer account
     */
    void deactivateBuyer(Long buyerId, String reason);
    
    /**
     * Suspend buyer account
     */
    void suspendBuyer(Long buyerId, String reason, LocalDateTime suspensionEndDate);
    
    /**
     * Update buyer status
     */
    void updateBuyerStatus(Long buyerId, Buyer.BuyerStatus status, String reason);

    // ===============================
    // SUBSCRIPTION & PREMIUM MANAGEMENT
    // ===============================
    
    /**
     * Upgrade buyer to premium
     */
    void upgradeToPremium(Long buyerId, Buyer.SubscriptionType subscriptionType);
    
    /**
     * Downgrade from premium
     */
    void downgradeFromPremium(Long buyerId);
    
    /**
     * Renew subscription
     */
    void renewSubscription(Long buyerId, Buyer.SubscriptionType subscriptionType);
    
    /**
     * Cancel subscription
     */
    void cancelSubscription(Long buyerId, String reason);
    
    /**
     * Update subscription preferences
     */
    void updateSubscriptionPreferences(Long buyerId, Boolean autoRenew, 
                                       Buyer.SubscriptionType preferredType);

    // ===============================
    // PROFILE MANAGEMENT
    // ===============================
    
    /**
     * Update personal information
     */
    void updatePersonalInfo(Long buyerId, String firstName, String lastName, 
                           String displayName, String bio);
    
    /**
     * Update contact information
     */
    void updateContactInfo(Long buyerId, String email, String phone, 
                          String secondaryEmail, String secondaryPhone);
    
    /**
     * Update business information
     */
    void updateBusinessInfo(Long buyerId, Buyer.BusinessType businessType, 
                           List<Buyer.Industry> industries, Buyer.CompanySize companySize,
                           BigDecimal annualBudget, Buyer.PurchasingAuthority purchasingAuthority);
    
    /**
     * Update billing address
     */
    void updateBillingAddress(Long buyerId, String addressLine1, String addressLine2,
                             String city, String state, String postalCode, String country);
    
    /**
     * Update shipping address
     */
    void updateShippingAddress(Long buyerId, String addressLine1, String addressLine2,
                              String city, String state, String postalCode, String country);
    
    /**
     * Update communication preferences
     */
    void updateCommunicationPreferences(Long buyerId, Boolean emailNotifications,
                                       Boolean smsNotifications, Boolean marketingEmails,
                                       Boolean priceAlerts, Boolean newProductAlerts);
    
    /**
     * Update privacy settings
     */
    void updatePrivacySettings(Long buyerId, Buyer.ProfileVisibility profileVisibility,
                              Boolean twoFactorEnabled);

    // ===============================
    // SEARCH AND FILTERING
    // ===============================
    
    /**
     * Get all buyers with pagination
     */
    Page<BuyerDto> getAllBuyers(Pageable pageable);
    
    /**
     * Search buyers by text
     */
    Page<BuyerDto> searchBuyers(String searchTerm, Pageable pageable);
    
    /**
     * Filter buyers with multiple criteria
     */
    Page<BuyerDto> filterBuyers(Buyer.BuyerType buyerType, Buyer.BuyerStatus status,
                               Buyer.BusinessType businessType, Buyer.CompanySize companySize,
                               String city, String state, Boolean isPremium, 
                               Boolean isVerified, Pageable pageable);
    
    /**
     * Get buyers by status
     */
    Page<BuyerDto> getBuyersByStatus(Buyer.BuyerStatus status, Pageable pageable);
    
    /**
     * Get buyers by type
     */
    Page<BuyerDto> getBuyersByType(Buyer.BuyerType buyerType, Pageable pageable);
    
    /**
     * Get buyers by location
     */
    Page<BuyerDto> getBuyersByLocation(String city, String state, Pageable pageable);
    
    /**
     * Get premium buyers
     */
    Page<BuyerDto> getPremiumBuyers(Pageable pageable);
    
    /**
     * Get verified buyers
     */
    List<BuyerDto> getVerifiedBuyers();
    
    /**
     * Get buyers by company
     */
    Page<BuyerDto> getBuyersByCompany(Long companyId, Pageable pageable);

    // ===============================
    // ANALYTICS AND REPORTING
    // ===============================
    
    /**
     * Get buyer count by status
     */
    Map<Buyer.BuyerStatus, Long> getBuyerCountByStatus();
    
    /**
     * Get buyer count by type
     */
    Map<Buyer.BuyerType, Long> getBuyerCountByType();
    
    /**
     * Get buyer count by verification status
     */
    Map<VerificationStatus, Long> getBuyerCountByVerificationStatus();
    
    /**
     * Get geographic distribution
     */
    Map<String, Long> getBuyerCountByState();
    
    /**
     * Get buyer registration statistics
     */
    Map<String, Long> getBuyerRegistrationStats(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get premium buyer statistics
     */
    Map<String, Object> getPremiumBuyerStats();
    
    /**
     * Get buyer engagement metrics
     */
    Map<String, Object> getBuyerEngagementMetrics();
    
    /**
     * Get dashboard statistics
     */
    Map<String, Object> getBuyerDashboardStats();

    // ===============================
    // BUSINESS INTELLIGENCE
    // ===============================
    
    /**
     * Get high-value buyers
     */
    List<BuyerDto> getHighValueBuyers(BigDecimal minOrderValue);
    
    /**
     * Get frequent buyers
     */
    Page<BuyerDto> getFrequentBuyers(Integer minOrders, Pageable pageable);
    
    /**
     * Get inactive buyers for re-engagement
     */
    List<BuyerDto> getInactiveBuyersForReEngagement(Integer daysSinceLastLogin);
    
    /**
     * Get buyers eligible for premium offers
     */
    List<BuyerDto> getBuyersEligibleForPremiumOffers(BigDecimal minOrderValue);
    
    /**
     * Get buyers with pending KYC
     */
    List<BuyerDto> getBuyersWithPendingKyc(Integer daysSinceRegistration);
    
    /**
     * Get active buyers by time period
     */
    List<BuyerDto> getActiveBuyersSince(LocalDateTime sinceDate);

    // ===============================
    // BULK OPERATIONS
    // ===============================
    
    /**
     * Bulk update buyer status
     */
    void bulkUpdateStatus(List<Long> buyerIds, Buyer.BuyerStatus status, String reason);
    
    /**
     * Bulk send notifications
     */
    void bulkSendNotification(List<Long> buyerIds, String subject, String message, String type);
    
    /**
     * Bulk export buyers
     */
    byte[] bulkExportBuyers(List<Long> buyerIds, String format);
    
    /**
     * Import buyers from file
     */
    Map<String, Object> importBuyers(byte[] fileData, String format);

    // ===============================
    // COMMUNICATION MANAGEMENT
    // ===============================
    
    /**
     * Get buyers for email notifications
     */
    List<BuyerDto> getBuyersForEmailNotifications();
    
    /**
     * Get buyers for SMS notifications
     */
    List<BuyerDto> getBuyersForSmsNotifications();
    
    /**
     * Get buyers for marketing emails
     */
    List<BuyerDto> getBuyersForMarketingEmails();
    
    /**
     * Get buyers by preferred category
     */
    List<BuyerDto> getBuyersByPreferredCategory(String category);
    
    /**
     * Update marketing preferences
     */
    void updateMarketingPreferences(Long buyerId, Boolean allowMarketingEmails,
                                   Boolean allowPromotionalSms);

    // ===============================
    // INTEGRATION AND EXTERNAL SERVICES
    // ===============================
    
    /**
     * Sync buyer with external CRM
     */
    void syncWithExternalCRM(Long buyerId);
    
    /**
     * Update buyer credit limit from external service
     */
    void updateCreditLimitFromExternal(Long buyerId);
    
    /**
     * Validate buyer with external fraud service
     */
    Map<String, Object> validateBuyerWithFraudService(Long buyerId);

    // ===============================
    // ACTIVITY TRACKING
    // ===============================
    
    /**
     * Track buyer activity
     */
    void trackBuyerActivity(Long buyerId, String activityType, Map<String, Object> activityData);
    
    /**
     * Update buyer engagement score
     */
    void updateEngagementScore(Long buyerId);
    
    /**
     * Update buyer tier based on activity
     */
    void updateBuyerTier(Long buyerId);

    // ===============================
    // VALIDATION AND UTILITY METHODS
    // ===============================
    
    /**
     * Check if email is available
     */
    boolean isEmailAvailable(String email);
    
    /**
     * Check if phone is available
     */
    boolean isPhoneAvailable(String phone);
    
    /**
     * Validate buyer data
     */
    Map<String, String> validateBuyerData(CreateBuyerDto createBuyerDto);
    
    /**
     * Check buyer eligibility for feature
     */
    boolean checkFeatureEligibility(Long buyerId, String featureName);
    
    /**
     * Get buyer summary for display
     */
    Map<String, Object> getBuyerSummary(Long buyerId);
}


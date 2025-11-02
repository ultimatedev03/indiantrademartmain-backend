package com.itech.itech_backend.modules.buyer.repository;

import com.itech.itech_backend.modules.buyer.model.Buyer;
import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.enums.KycStatus;
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
public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    // ===============================
    // BASIC FINDER METHODS
    // ===============================
    
    Optional<Buyer> findByEmail(String email);
    Optional<Buyer> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    @Query("SELECT b FROM Buyer b WHERE b.email = :emailOrPhone OR b.phone = :emailOrPhone")
    Optional<Buyer> findByEmailOrPhone(@Param("emailOrPhone") String emailOrPhone1, @Param("emailOrPhone") String emailOrPhone2);
    
    // Migration support methods
    List<Buyer> findByUserIsNull();
    long countByUserIsNull();
    
    Optional<Buyer> findByEmailAndBuyerStatus(String email, Buyer.BuyerStatus status);
    Optional<Buyer> findByPhoneAndBuyerStatus(String phone, Buyer.BuyerStatus status);

    // ===============================
    // STATUS AND TYPE BASED QUERIES
    // ===============================
    
    List<Buyer> findByBuyerStatus(Buyer.BuyerStatus status);
    Page<Buyer> findByBuyerStatus(Buyer.BuyerStatus status, Pageable pageable);
    
    List<Buyer> findByBuyerType(Buyer.BuyerType buyerType);
    Page<Buyer> findByBuyerType(Buyer.BuyerType buyerType, Pageable pageable);
    
    Page<Buyer> findByBuyerStatusAndBuyerType(Buyer.BuyerStatus status, Buyer.BuyerType buyerType, Pageable pageable);
    
    // ===============================
    // VERIFICATION STATUS QUERIES
    // ===============================
    
    List<Buyer> findByIsEmailVerified(Boolean isEmailVerified);
    List<Buyer> findByIsPhoneVerified(Boolean isPhoneVerified);
    List<Buyer> findByIsKycVerified(Boolean isKycVerified);
    List<Buyer> findByVerificationStatus(VerificationStatus verificationStatus);
    
    Page<Buyer> findByVerificationStatus(VerificationStatus verificationStatus, Pageable pageable);
    
    @Query("SELECT b FROM Buyer b WHERE b.isEmailVerified = true AND b.isPhoneVerified = true")
    List<Buyer> findFullyVerifiedBuyers();
    
    @Query("SELECT b FROM Buyer b WHERE b.isEmailVerified = false OR b.isPhoneVerified = false")
    List<Buyer> findUnverifiedBuyers();

    // ===============================
    // COMPANY ASSOCIATION QUERIES
    // ===============================
    
    List<Buyer> findByCompany_Id(Long companyId);
    Page<Buyer> findByCompany_Id(Long companyId, Pageable pageable);
    
    @Query("SELECT b FROM Buyer b WHERE b.company.id = :companyId AND b.buyerStatus = :status")
    List<Buyer> findByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") Buyer.BuyerStatus status);

    // ===============================
    // BUSINESS INFORMATION QUERIES
    // ===============================
    
    List<Buyer> findByBusinessType(Buyer.BusinessType businessType);
    List<Buyer> findByCompanySize(Buyer.CompanySize companySize);
    List<Buyer> findByPurchasingAuthority(Buyer.PurchasingAuthority purchasingAuthority);
    
    @Query("SELECT b FROM Buyer b WHERE :industry MEMBER OF b.industries")
    List<Buyer> findByIndustry(@Param("industry") Buyer.Industry industry);
    
    @Query("SELECT b FROM Buyer b WHERE b.annualBudget >= :minBudget AND b.annualBudget <= :maxBudget")
    List<Buyer> findByBudgetRange(@Param("minBudget") BigDecimal minBudget, @Param("maxBudget") BigDecimal maxBudget);

    // ===============================
    // SUBSCRIPTION AND PREMIUM QUERIES
    // ===============================
    
    List<Buyer> findBySubscriptionType(Buyer.SubscriptionType subscriptionType);
    List<Buyer> findByIsPremium(Boolean isPremium);
    
    @Query("SELECT b FROM Buyer b WHERE b.subscriptionExpiryDate < :currentDate")
    List<Buyer> findExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT b FROM Buyer b WHERE b.subscriptionExpiryDate BETWEEN :startDate AND :endDate")
    List<Buyer> findSubscriptionsExpiringBetween(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    // ===============================
    // LOCATION-BASED QUERIES
    // ===============================
    
    List<Buyer> findByBillingCity(String city);
    List<Buyer> findByBillingState(String state);
    List<Buyer> findByBillingCountry(String country);
    
    Page<Buyer> findByBillingCity(String city, Pageable pageable);
    Page<Buyer> findByBillingState(String state, Pageable pageable);
    
    @Query("SELECT DISTINCT b.billingCity FROM Buyer b WHERE b.billingCountry = :country ORDER BY b.billingCity")
    List<String> findDistinctCitiesByCountry(@Param("country") String country);
    
    @Query("SELECT DISTINCT b.billingState FROM Buyer b WHERE b.billingCountry = :country ORDER BY b.billingState")
    List<String> findDistinctStatesByCountry(@Param("country") String country);

    // ===============================
    // SEARCH AND FILTERING QUERIES
    // ===============================
    
    @Query("SELECT b FROM Buyer b WHERE " +
           "LOWER(b.buyerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Buyer> searchBuyers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT b FROM Buyer b WHERE " +
           "(:buyerType IS NULL OR b.buyerType = :buyerType) AND " +
           "(:status IS NULL OR b.buyerStatus = :status) AND " +
           "(:businessType IS NULL OR b.businessType = :businessType) AND " +
           "(:companySize IS NULL OR b.companySize = :companySize) AND " +
           "(:city IS NULL OR LOWER(b.billingCity) = LOWER(:city)) AND " +
           "(:state IS NULL OR LOWER(b.billingState) = LOWER(:state)) AND " +
           "(:isPremium IS NULL OR b.isPremium = :isPremium) AND " +
           "(:isVerified IS NULL OR (b.isEmailVerified = :isVerified AND b.isPhoneVerified = :isVerified))")
    Page<Buyer> findBuyersWithFilters(@Param("buyerType") Buyer.BuyerType buyerType,
                                      @Param("status") Buyer.BuyerStatus status,
                                      @Param("businessType") Buyer.BusinessType businessType,
                                      @Param("companySize") Buyer.CompanySize companySize,
                                      @Param("city") String city,
                                      @Param("state") String state,
                                      @Param("isPremium") Boolean isPremium,
                                      @Param("isVerified") Boolean isVerified,
                                      Pageable pageable);

    // ===============================
    // ANALYTICS AND STATISTICS QUERIES
    // ===============================
    
    @Query("SELECT COUNT(b) FROM Buyer b WHERE b.buyerStatus = :status")
    long countByStatus(@Param("status") Buyer.BuyerStatus status);
    
    @Query("SELECT COUNT(b) FROM Buyer b WHERE b.buyerType = :buyerType")
    long countByBuyerType(@Param("buyerType") Buyer.BuyerType buyerType);
    
    @Query("SELECT COUNT(b) FROM Buyer b WHERE b.verificationStatus = :verificationStatus")
    long countByVerificationStatus(@Param("verificationStatus") VerificationStatus verificationStatus);
    
    @Query("SELECT COUNT(b) FROM Buyer b WHERE b.isPremium = true")
    long countPremiumBuyers();
    
    @Query("SELECT COUNT(b) FROM Buyer b WHERE b.createdAt >= :startDate AND b.createdAt <= :endDate")
    long countBuyersRegisteredBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b.billingState, COUNT(b) FROM Buyer b GROUP BY b.billingState ORDER BY COUNT(b) DESC")
    List<Object[]> getBuyerCountByState();
    
    @Query("SELECT b.billingCity, COUNT(b) FROM Buyer b GROUP BY b.billingCity ORDER BY COUNT(b) DESC")
    List<Object[]> getBuyerCountByCity();
    
    @Query("SELECT b.buyerType, COUNT(b) FROM Buyer b GROUP BY b.buyerType")
    List<Object[]> getBuyerCountByType();
    
    @Query("SELECT b.businessType, COUNT(b) FROM Buyer b WHERE b.businessType IS NOT NULL GROUP BY b.businessType")
    List<Object[]> getBuyerCountByBusinessType();

    // ===============================
    // ACTIVITY AND ENGAGEMENT QUERIES
    // ===============================
    
    @Query("SELECT b FROM Buyer b WHERE b.lastLoginDate >= :sinceDate ORDER BY b.lastLoginDate DESC")
    List<Buyer> findActiveBuyersSince(@Param("sinceDate") LocalDateTime sinceDate);
    
    @Query("SELECT b FROM Buyer b WHERE b.lastLoginDate < :beforeDate OR b.lastLoginDate IS NULL")
    List<Buyer> findInactiveBuyersBefore(@Param("beforeDate") LocalDateTime beforeDate);
    
    @Query("SELECT b FROM Buyer b WHERE b.totalOrderValue >= :minOrderValue ORDER BY b.totalOrderValue DESC")
    List<Buyer> findHighValueBuyers(@Param("minOrderValue") BigDecimal minOrderValue);
    
    @Query("SELECT b FROM Buyer b WHERE b.totalOrders >= :minOrders ORDER BY b.totalOrders DESC")
    Page<Buyer> findFrequentBuyers(@Param("minOrders") Integer minOrders, Pageable pageable);

    // ===============================
    // COMMUNICATION AND PREFERENCES
    // ===============================
    
    @Query("SELECT b FROM Buyer b WHERE b.emailNotifications = true AND b.buyerStatus = 'ACTIVE'")
    List<Buyer> findBuyersForEmailNotifications();
    
    @Query("SELECT b FROM Buyer b WHERE b.smsNotifications = true AND b.buyerStatus = 'ACTIVE'")
    List<Buyer> findBuyersForSmsNotifications();
    
    @Query("SELECT b FROM Buyer b WHERE b.marketingEmails = true AND b.buyerStatus = 'ACTIVE'")
    List<Buyer> findBuyersForMarketingEmails();
    
    @Query("SELECT b FROM Buyer b WHERE :category MEMBER OF b.preferredCategories")
    List<Buyer> findBuyersByPreferredCategory(@Param("category") String category);

    // ===============================
    // DATE RANGE QUERIES
    // ===============================
    
    List<Buyer> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Buyer> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT b FROM Buyer b WHERE b.createdAt >= :startOfDay AND b.createdAt < :endOfDay")
    List<Buyer> findBuyersRegisteredToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    // Alternative method using native SQL for better compatibility
    @Query(value = "SELECT * FROM buyers WHERE created_at >= CURRENT_DATE", nativeQuery = true)
    List<Buyer> findBuyersRegisteredTodayNative();
    
    @Query("SELECT b FROM Buyer b WHERE b.createdAt >= :startOfWeek")
    List<Buyer> findBuyersRegisteredThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek);
    
    @Query("SELECT b FROM Buyer b WHERE b.createdAt >= :startOfMonth")
    List<Buyer> findBuyersRegisteredThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth);

    // ===============================
    // BULK OPERATIONS SUPPORT
    // ===============================
    
    @Query("SELECT b FROM Buyer b WHERE b.id IN :buyerIds")
    List<Buyer> findByIdIn(@Param("buyerIds") List<Long> buyerIds);
    
    @Query("SELECT b FROM Buyer b WHERE b.email IN :emails")
    List<Buyer> findByEmailIn(@Param("emails") List<String> emails);

    // ===============================
    // CUSTOM BUSINESS LOGIC QUERIES
    // ===============================
    
    @Query("SELECT b FROM Buyer b WHERE " +
           "b.buyerStatus = 'ACTIVE' AND " +
           "b.isEmailVerified = true AND " +
           "b.isPhoneVerified = true AND " +
           "(b.isPremium = true OR b.totalOrderValue >= :minOrderValue)")
    List<Buyer> findEligibleForPremiumOffers(@Param("minOrderValue") BigDecimal minOrderValue);
    
    @Query("SELECT b FROM Buyer b WHERE " +
           "b.buyerStatus = 'ACTIVE' AND " +
           "b.lastLoginDate < :cutoffDate AND " +
           "b.totalOrders > 0")
    List<Buyer> findBuyersForReEngagement(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT b FROM Buyer b WHERE " +
           "b.kycStatus = 'PENDING' AND " +
           "b.createdAt < :cutoffDate")
    List<Buyer> findBuyersWithPendingKyc(@Param("cutoffDate") LocalDateTime cutoffDate);

    // ===============================
    // SORTING AND ORDERING QUERIES
    // ===============================
    
    List<Buyer> findAllByOrderByCreatedAtDesc();
    List<Buyer> findAllByOrderByTotalOrderValueDesc();
    List<Buyer> findAllByOrderByTotalOrdersDesc();
    List<Buyer> findAllByOrderByLastLoginDateDesc();
    
    @Query("SELECT b FROM Buyer b ORDER BY " +
           "CASE WHEN b.isPremium = true THEN 0 ELSE 1 END, " +
           "b.totalOrderValue DESC, " +
           "b.createdAt DESC")
    Page<Buyer> findAllOrderedByPriorityAndValue(Pageable pageable);

    // ===============================
    // DASHBOARD AND REPORTING QUERIES
    // ===============================
    
    @Query("SELECT " +
           "COUNT(b), " +
           "COUNT(CASE WHEN b.buyerStatus = 'ACTIVE' THEN 1 END), " +
           "COUNT(CASE WHEN b.isPremium = true THEN 1 END), " +
           "COUNT(CASE WHEN b.isEmailVerified = true AND b.isPhoneVerified = true THEN 1 END) " +
           "FROM Buyer b")
    Object[] getBuyerDashboardStats();
    
    @Query("SELECT " +
           "SUM(b.totalOrderValue), " +
           "AVG(b.totalOrderValue), " +
           "SUM(b.totalOrders), " +
           "AVG(b.totalOrders) " +
           "FROM Buyer b WHERE b.buyerStatus = 'ACTIVE'")
    Object[] getBuyerBusinessStats();
}


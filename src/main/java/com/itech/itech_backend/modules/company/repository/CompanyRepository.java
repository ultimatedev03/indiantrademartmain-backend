package com.itech.itech_backend.modules.company.repository;

import com.itech.itech_backend.modules.company.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    // Find by unique identifiers
    Optional<Company> findByGstNumber(String gstNumber);
    Optional<Company> findByPanNumber(String panNumber);
    Optional<Company> findByCinNumber(String cinNumber);
    Optional<Company> findByPrimaryEmail(String primaryEmail);
    
    // Existence checks for unique fields
    boolean existsByGstNumber(String gstNumber);
    boolean existsByPanNumber(String panNumber);
    boolean existsByCinNumber(String cinNumber);
    boolean existsByPrimaryEmail(String primaryEmail);
    
    // Find by verification status
    List<Company> findByVerificationStatus(Company.VerificationStatus verificationStatus);
    Page<Company> findByVerificationStatus(Company.VerificationStatus verificationStatus, Pageable pageable);
    
    // Find by company status
    List<Company> findByStatus(Company.CompanyStatus status);
    Page<Company> findByStatus(Company.CompanyStatus status, Pageable pageable);
    
    // Find verified companies
    List<Company> findByIsVerified(Boolean isVerified);
    Page<Company> findByIsVerified(Boolean isVerified, Pageable pageable);
    
    // Find premium companies
    List<Company> findByIsPremium(Boolean isPremium);
    Page<Company> findByIsPremium(Boolean isPremium, Pageable pageable);
    
    // Find by subscription type
    List<Company> findBySubscriptionType(Company.SubscriptionType subscriptionType);
    Page<Company> findBySubscriptionType(Company.SubscriptionType subscriptionType, Pageable pageable);
    
    // Find by company type
    List<Company> findByCompanyType(Company.CompanyType companyType);
    Page<Company> findByCompanyType(Company.CompanyType companyType, Pageable pageable);
    
    // Find by business category
    List<Company> findByBusinessCategory(Company.BusinessCategory businessCategory);
    Page<Company> findByBusinessCategory(Company.BusinessCategory businessCategory, Pageable pageable);
    
    // Find by location
    List<Company> findByCity(String city);
    List<Company> findByState(String state);
    List<Company> findByCityAndState(String city, String state);
    Page<Company> findByCity(String city, Pageable pageable);
    Page<Company> findByState(String state, Pageable pageable);
    
    // Search functionality
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.legalName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Company> searchCompanies(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Advanced search with filters
    @Query("SELECT c FROM Company c WHERE " +
           "(:name IS NULL OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:city IS NULL OR LOWER(c.city) = LOWER(:city)) AND " +
           "(:state IS NULL OR LOWER(c.state) = LOWER(:state)) AND " +
           "(:companyType IS NULL OR c.companyType = :companyType) AND " +
           "(:businessCategory IS NULL OR c.businessCategory = :businessCategory) AND " +
           "(:verificationStatus IS NULL OR c.verificationStatus = :verificationStatus) AND " +
           "(:isVerified IS NULL OR c.isVerified = :isVerified) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Company> findCompaniesWithFilters(
        @Param("name") String name,
        @Param("city") String city,
        @Param("state") String state,
        @Param("companyType") Company.CompanyType companyType,
        @Param("businessCategory") Company.BusinessCategory businessCategory,
        @Param("verificationStatus") Company.VerificationStatus verificationStatus,
        @Param("isVerified") Boolean isVerified,
        @Param("status") Company.CompanyStatus status,
        Pageable pageable
    );
    
    // Statistics queries
    @Query("SELECT COUNT(c) FROM Company c WHERE c.verificationStatus = :status")
    long countByVerificationStatus(@Param("status") Company.VerificationStatus status);
    
    @Query("SELECT COUNT(c) FROM Company c WHERE c.isVerified = true")
    long countVerifiedCompanies();
    
    @Query("SELECT COUNT(c) FROM Company c WHERE c.isPremium = true")
    long countPremiumCompanies();
    
    @Query("SELECT COUNT(c) FROM Company c WHERE c.status = :status")
    long countByStatus(@Param("status") Company.CompanyStatus status);
    
    // Companies by employee count ranges
    List<Company> findByEmployeeCount(Company.EmployeeCount employeeCount);
    Page<Company> findByEmployeeCount(Company.EmployeeCount employeeCount, Pageable pageable);
    
    // Companies by annual turnover ranges
    List<Company> findByAnnualTurnover(Company.AnnualTurnover annualTurnover);
    Page<Company> findByAnnualTurnover(Company.AnnualTurnover annualTurnover, Pageable pageable);
    
    // Recent companies
    @Query("SELECT c FROM Company c ORDER BY c.createdAt DESC")
    Page<Company> findRecentCompanies(Pageable pageable);
    
    // Companies requiring verification
    @Query("SELECT c FROM Company c WHERE c.verificationStatus = 'PENDING' ORDER BY c.createdAt ASC")
    Page<Company> findPendingVerificationCompanies(Pageable pageable);
    
    // Top companies by verification status and premium status
    @Query("SELECT c FROM Company c WHERE c.isVerified = true AND c.isPremium = true ORDER BY c.createdAt DESC")
    Page<Company> findTopVerifiedPremiumCompanies(Pageable pageable);
}


package com.itech.itech_backend.modules.company.service;

import com.itech.itech_backend.modules.company.dto.*;
import com.itech.itech_backend.modules.company.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CompanyService {
    
    // CRUD Operations
    CompanyDto createCompany(CreateCompanyDto createCompanyDto);
    CompanyDto getCompanyById(Long companyId);
    CompanyDto updateCompany(Long companyId, UpdateCompanyDto updateCompanyDto);
    void deleteCompany(Long companyId);
    
    // Company listing and search
    Page<CompanyDto> getAllCompanies(Pageable pageable);
    Page<CompanyDto> searchCompanies(String searchTerm, Pageable pageable);
    Page<CompanyDto> getCompaniesWithFilters(String name, String city, String state, 
                                           Company.CompanyType companyType, 
                                           Company.BusinessCategory businessCategory,
                                           Company.VerificationStatus verificationStatus, 
                                           Boolean isVerified, 
                                           Company.CompanyStatus status, 
                                           Pageable pageable);
    
    // Company verification
    CompanyDto verifyCompany(CompanyVerificationDto verificationDto);
    Page<CompanyDto> getPendingVerificationCompanies(Pageable pageable);
    Page<CompanyDto> getCompaniesByVerificationStatus(Company.VerificationStatus status, Pageable pageable);
    
    // Company status management
    CompanyDto updateCompanyStatus(Long companyId, Company.CompanyStatus status);
    Page<CompanyDto> getCompaniesByStatus(Company.CompanyStatus status, Pageable pageable);
    
    // Location-based queries
    List<CompanyDto> getCompaniesByCity(String city);
    List<CompanyDto> getCompaniesByState(String state);
    List<CompanyDto> getCompaniesByCityAndState(String city, String state);
    
    // Company categories and types
    Page<CompanyDto> getCompaniesByType(Company.CompanyType companyType, Pageable pageable);
    Page<CompanyDto> getCompaniesByBusinessCategory(Company.BusinessCategory businessCategory, Pageable pageable);
    Page<CompanyDto> getCompaniesByEmployeeCount(Company.EmployeeCount employeeCount, Pageable pageable);
    Page<CompanyDto> getCompaniesByAnnualTurnover(Company.AnnualTurnover annualTurnover, Pageable pageable);
    
    // Premium and subscription management
    Page<CompanyDto> getPremiumCompanies(Pageable pageable);
    Page<CompanyDto> getCompaniesBySubscriptionType(Company.SubscriptionType subscriptionType, Pageable pageable);
    CompanyDto upgradeToPremium(Long companyId, Company.SubscriptionType subscriptionType);
    CompanyDto downgradePremium(Long companyId);
    
    // Verification and validation
    boolean isGstNumberUnique(String gstNumber);
    boolean isPanNumberUnique(String panNumber);
    boolean isCinNumberUnique(String cinNumber);
    boolean isEmailUnique(String email);
    CompanyDto getCompanyByGstNumber(String gstNumber);
    CompanyDto getCompanyByPanNumber(String panNumber);
    CompanyDto getCompanyByEmail(String email);
    
    // Statistics and analytics
    Map<String, Long> getCompanyStatistics();
    long getTotalCompaniesCount();
    long getVerifiedCompaniesCount();
    long getPremiumCompaniesCount();
    Map<Company.VerificationStatus, Long> getCompanyCountByVerificationStatus();
    Map<Company.CompanyStatus, Long> getCompanyCountByStatus();
    Map<Company.CompanyType, Long> getCompanyCountByType();
    Map<Company.BusinessCategory, Long> getCompanyCountByCategory();
    
    // Recent and trending companies
    Page<CompanyDto> getRecentCompanies(Pageable pageable);
    Page<CompanyDto> getTopVerifiedPremiumCompanies(Pageable pageable);
    
    // Bulk operations
    List<CompanyDto> createCompaniesInBulk(List<CreateCompanyDto> createCompanyDtos);
    Map<String, Object> bulkUpdateCompanyStatus(List<Long> companyIds, Company.CompanyStatus status);
    Map<String, Object> bulkVerifyCompanies(List<Long> companyIds, Company.VerificationStatus status);
    
    // Export functionality
    byte[] exportCompaniesToExcel(List<Long> companyIds);
    byte[] exportCompaniesToPdf(List<Long> companyIds);
    
    // Company profile management
    CompanyDto updateCompanyLogo(Long companyId, String logoUrl);
    CompanyDto addCompanyImages(Long companyId, List<String> imageUrls);
    CompanyDto removeCompanyImage(Long companyId, String imageUrl);
    CompanyDto updateBankingInfo(Long companyId, String bankName, String accountNumber, String ifscCode, String accountHolderName);
}


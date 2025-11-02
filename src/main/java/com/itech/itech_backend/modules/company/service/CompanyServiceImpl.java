package com.itech.itech_backend.modules.company.service;

import com.itech.itech_backend.modules.company.dto.*;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.company.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {
    
    private final CompanyRepository companyRepository;
    
    // CRUD Operations
    @Override
    public CompanyDto createCompany(CreateCompanyDto createCompanyDto) {
        log.info("Creating new company: {}", createCompanyDto.getName());
        
        // Validate unique fields
        validateUniqueFields(createCompanyDto);
        
        Company company = new Company();
        BeanUtils.copyProperties(createCompanyDto, company);
        company.setCreatedBy("SYSTEM"); // In real implementation, get from SecurityContext
        
        Company savedCompany = companyRepository.save(company);
        log.info("Company created successfully with ID: {}", savedCompany.getId());
        
        return convertToDto(savedCompany);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyById(Long companyId) {
        log.debug("Fetching company with ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        return convertToDto(company);
    }
    
    @Override
    public CompanyDto updateCompany(Long companyId, UpdateCompanyDto updateCompanyDto) {
        log.info("Updating company with ID: {}", companyId);
        
        Company existingCompany = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        // Validate unique fields if they are being updated
        validateUniqueFieldsForUpdate(existingCompany, updateCompanyDto);
        
        // Update only non-null fields
        updateCompanyFields(existingCompany, updateCompanyDto);
        existingCompany.setUpdatedBy("SYSTEM"); // In real implementation, get from SecurityContext
        
        Company updatedCompany = companyRepository.save(existingCompany);
        log.info("Company updated successfully with ID: {}", updatedCompany.getId());
        
        return convertToDto(updatedCompany);
    }
    
    @Override
    public void deleteCompany(Long companyId) {
        log.info("Deleting company with ID: {}", companyId);
        
        if (!companyRepository.existsById(companyId)) {
            throw new EntityNotFoundException("Company not found with ID: " + companyId);
        }
        
        companyRepository.deleteById(companyId);
        log.info("Company deleted successfully with ID: {}", companyId);
    }
    
    // Company listing and search
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getAllCompanies(Pageable pageable) {
        log.debug("Fetching all companies with pagination");
        
        Page<Company> companies = companyRepository.findAll(pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> searchCompanies(String searchTerm, Pageable pageable) {
        log.debug("Searching companies with term: {}", searchTerm);
        
        Page<Company> companies = companyRepository.searchCompanies(searchTerm, pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesWithFilters(String name, String city, String state,
                                                   Company.CompanyType companyType,
                                                   Company.BusinessCategory businessCategory,
                                                   Company.VerificationStatus verificationStatus,
                                                   Boolean isVerified,
                                                   Company.CompanyStatus status,
                                                   Pageable pageable) {
        log.debug("Fetching companies with filters");
        
        Page<Company> companies = companyRepository.findCompaniesWithFilters(
            name, city, state, companyType, businessCategory, verificationStatus, isVerified, status, pageable
        );
        return companies.map(this::convertToDto);
    }
    
    // Company verification
    @Override
    public CompanyDto verifyCompany(CompanyVerificationDto verificationDto) {
        log.info("Processing company verification for ID: {}", verificationDto.getCompanyId());
        
        Company company = companyRepository.findById(verificationDto.getCompanyId())
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + verificationDto.getCompanyId()));
        
        company.setVerificationStatus(verificationDto.getVerificationStatus());
        company.setVerifiedBy(verificationDto.getVerifiedBy());
        
        if (verificationDto.getVerificationStatus() == Company.VerificationStatus.VERIFIED) {
            company.setIsVerified(true);
            company.setVerifiedAt(LocalDateTime.now());
        } else {
            company.setIsVerified(false);
            company.setVerifiedAt(null);
        }
        
        Company verifiedCompany = companyRepository.save(company);
        log.info("Company verification completed for ID: {}", verifiedCompany.getId());
        
        return convertToDto(verifiedCompany);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getPendingVerificationCompanies(Pageable pageable) {
        log.debug("Fetching pending verification companies");
        
        Page<Company> companies = companyRepository.findPendingVerificationCompanies(pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesByVerificationStatus(Company.VerificationStatus status, Pageable pageable) {
        log.debug("Fetching companies by verification status: {}", status);
        
        Page<Company> companies = companyRepository.findByVerificationStatus(status, pageable);
        return companies.map(this::convertToDto);
    }
    
    // Company status management
    @Override
    public CompanyDto updateCompanyStatus(Long companyId, Company.CompanyStatus status) {
        log.info("Updating company status for ID: {} to {}", companyId, status);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        company.setStatus(status);
        company.setUpdatedBy("SYSTEM");
        
        Company updatedCompany = companyRepository.save(company);
        log.info("Company status updated successfully for ID: {}", updatedCompany.getId());
        
        return convertToDto(updatedCompany);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesByStatus(Company.CompanyStatus status, Pageable pageable) {
        log.debug("Fetching companies by status: {}", status);
        
        Page<Company> companies = companyRepository.findByStatus(status, pageable);
        return companies.map(this::convertToDto);
    }
    
    // Location-based queries
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> getCompaniesByCity(String city) {
        log.debug("Fetching companies by city: {}", city);
        
        List<Company> companies = companyRepository.findByCity(city);
        return companies.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> getCompaniesByState(String state) {
        log.debug("Fetching companies by state: {}", state);
        
        List<Company> companies = companyRepository.findByState(state);
        return companies.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> getCompaniesByCityAndState(String city, String state) {
        log.debug("Fetching companies by city: {} and state: {}", city, state);
        
        List<Company> companies = companyRepository.findByCityAndState(city, state);
        return companies.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Company categories and types
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesByType(Company.CompanyType companyType, Pageable pageable) {
        log.debug("Fetching companies by type: {}", companyType);
        
        Page<Company> companies = companyRepository.findByCompanyType(companyType, pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesByBusinessCategory(Company.BusinessCategory businessCategory, Pageable pageable) {
        log.debug("Fetching companies by business category: {}", businessCategory);
        
        Page<Company> companies = companyRepository.findByBusinessCategory(businessCategory, pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesByEmployeeCount(Company.EmployeeCount employeeCount, Pageable pageable) {
        log.debug("Fetching companies by employee count: {}", employeeCount);
        
        Page<Company> companies = companyRepository.findByEmployeeCount(employeeCount, pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesByAnnualTurnover(Company.AnnualTurnover annualTurnover, Pageable pageable) {
        log.debug("Fetching companies by annual turnover: {}", annualTurnover);
        
        Page<Company> companies = companyRepository.findByAnnualTurnover(annualTurnover, pageable);
        return companies.map(this::convertToDto);
    }
    
    // Premium and subscription management
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getPremiumCompanies(Pageable pageable) {
        log.debug("Fetching premium companies");
        
        Page<Company> companies = companyRepository.findByIsPremium(true, pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getCompaniesBySubscriptionType(Company.SubscriptionType subscriptionType, Pageable pageable) {
        log.debug("Fetching companies by subscription type: {}", subscriptionType);
        
        Page<Company> companies = companyRepository.findBySubscriptionType(subscriptionType, pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    public CompanyDto upgradeToPremium(Long companyId, Company.SubscriptionType subscriptionType) {
        log.info("Upgrading company to premium - ID: {}, Subscription: {}", companyId, subscriptionType);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        company.setIsPremium(true);
        company.setSubscriptionType(subscriptionType);
        company.setPremiumExpiresAt(LocalDateTime.now().plusMonths(12)); // 1 year subscription
        company.setUpdatedBy("SYSTEM");
        
        Company updatedCompany = companyRepository.save(company);
        log.info("Company upgraded to premium successfully - ID: {}", updatedCompany.getId());
        
        return convertToDto(updatedCompany);
    }
    
    @Override
    public CompanyDto downgradePremium(Long companyId) {
        log.info("Downgrading company from premium - ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        company.setIsPremium(false);
        company.setSubscriptionType(Company.SubscriptionType.FREE);
        company.setPremiumExpiresAt(null);
        company.setUpdatedBy("SYSTEM");
        
        Company updatedCompany = companyRepository.save(company);
        log.info("Company downgraded from premium successfully - ID: {}", updatedCompany.getId());
        
        return convertToDto(updatedCompany);
    }
    
    // Verification and validation methods
    @Override
    @Transactional(readOnly = true)
    public boolean isGstNumberUnique(String gstNumber) {
        return gstNumber == null || !companyRepository.existsByGstNumber(gstNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPanNumberUnique(String panNumber) {
        return panNumber == null || !companyRepository.existsByPanNumber(panNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isCinNumberUnique(String cinNumber) {
        return cinNumber == null || !companyRepository.existsByCinNumber(cinNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailUnique(String email) {
        return email == null || !companyRepository.existsByPrimaryEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyByGstNumber(String gstNumber) {
        Company company = companyRepository.findByGstNumber(gstNumber)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with GST number: " + gstNumber));
        return convertToDto(company);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyByPanNumber(String panNumber) {
        Company company = companyRepository.findByPanNumber(panNumber)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with PAN number: " + panNumber));
        return convertToDto(company);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyByEmail(String email) {
        Company company = companyRepository.findByPrimaryEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with email: " + email));
        return convertToDto(company);
    }
    
    // Statistics and analytics
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCompanyStatistics() {
        log.debug("Generating company statistics");
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalCompanies", companyRepository.count());
        stats.put("verifiedCompanies", companyRepository.countVerifiedCompanies());
        stats.put("premiumCompanies", companyRepository.countPremiumCompanies());
        stats.put("pendingVerification", companyRepository.countByVerificationStatus(Company.VerificationStatus.PENDING));
        stats.put("activeCompanies", companyRepository.countByStatus(Company.CompanyStatus.ACTIVE));
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalCompaniesCount() {
        return companyRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getVerifiedCompaniesCount() {
        return companyRepository.countVerifiedCompanies();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getPremiumCompaniesCount() {
        return companyRepository.countPremiumCompanies();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Company.VerificationStatus, Long> getCompanyCountByVerificationStatus() {
        Map<Company.VerificationStatus, Long> counts = new HashMap<>();
        for (Company.VerificationStatus status : Company.VerificationStatus.values()) {
            counts.put(status, companyRepository.countByVerificationStatus(status));
        }
        return counts;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Company.CompanyStatus, Long> getCompanyCountByStatus() {
        Map<Company.CompanyStatus, Long> counts = new HashMap<>();
        for (Company.CompanyStatus status : Company.CompanyStatus.values()) {
            counts.put(status, companyRepository.countByStatus(status));
        }
        return counts;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Company.CompanyType, Long> getCompanyCountByType() {
        Map<Company.CompanyType, Long> counts = new HashMap<>();
        for (Company.CompanyType type : Company.CompanyType.values()) {
            List<Company> companies = companyRepository.findByCompanyType(type);
            counts.put(type, (long) companies.size());
        }
        return counts;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Company.BusinessCategory, Long> getCompanyCountByCategory() {
        Map<Company.BusinessCategory, Long> counts = new HashMap<>();
        for (Company.BusinessCategory category : Company.BusinessCategory.values()) {
            List<Company> companies = companyRepository.findByBusinessCategory(category);
            counts.put(category, (long) companies.size());
        }
        return counts;
    }
    
    // Recent and trending companies
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getRecentCompanies(Pageable pageable) {
        log.debug("Fetching recent companies");
        
        Page<Company> companies = companyRepository.findRecentCompanies(pageable);
        return companies.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDto> getTopVerifiedPremiumCompanies(Pageable pageable) {
        log.debug("Fetching top verified premium companies");
        
        Page<Company> companies = companyRepository.findTopVerifiedPremiumCompanies(pageable);
        return companies.map(this::convertToDto);
    }
    
    // Bulk operations
    @Override
    public List<CompanyDto> createCompaniesInBulk(List<CreateCompanyDto> createCompanyDtos) {
        log.info("Creating {} companies in bulk", createCompanyDtos.size());
        
        List<CompanyDto> createdCompanies = new ArrayList<>();
        for (CreateCompanyDto createDto : createCompanyDtos) {
            try {
                CompanyDto created = createCompany(createDto);
                createdCompanies.add(created);
            } catch (Exception e) {
                log.warn("Failed to create company in bulk: {}", createDto.getName(), e);
            }
        }
        
        log.info("Successfully created {} out of {} companies in bulk", createdCompanies.size(), createCompanyDtos.size());
        return createdCompanies;
    }
    
    @Override
    public Map<String, Object> bulkUpdateCompanyStatus(List<Long> companyIds, Company.CompanyStatus status) {
        log.info("Bulk updating company status for {} companies to {}", companyIds.size(), status);
        
        int successful = 0;
        int failed = 0;
        
        for (Long companyId : companyIds) {
            try {
                updateCompanyStatus(companyId, status);
                successful++;
            } catch (Exception e) {
                log.warn("Failed to update company status for ID: {}", companyId, e);
                failed++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successful", successful);
        result.put("failed", failed);
        result.put("total", companyIds.size());
        
        return result;
    }
    
    @Override
    public Map<String, Object> bulkVerifyCompanies(List<Long> companyIds, Company.VerificationStatus status) {
        log.info("Bulk verifying {} companies with status {}", companyIds.size(), status);
        
        int successful = 0;
        int failed = 0;
        
        for (Long companyId : companyIds) {
            try {
                CompanyVerificationDto verificationDto = new CompanyVerificationDto();
                verificationDto.setCompanyId(companyId);
                verificationDto.setVerificationStatus(status);
                verificationDto.setVerifiedBy("BULK_OPERATION");
                
                verifyCompany(verificationDto);
                successful++;
            } catch (Exception e) {
                log.warn("Failed to verify company for ID: {}", companyId, e);
                failed++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successful", successful);
        result.put("failed", failed);
        result.put("total", companyIds.size());
        
        return result;
    }
    
    // Export functionality (placeholder implementations)
    @Override
    @Transactional(readOnly = true)
    public byte[] exportCompaniesToExcel(List<Long> companyIds) {
        log.info("Exporting {} companies to Excel", companyIds.size());
        // TODO: Implement Excel export functionality
        throw new UnsupportedOperationException("Excel export not yet implemented");
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] exportCompaniesToPdf(List<Long> companyIds) {
        log.info("Exporting {} companies to PDF", companyIds.size());
        // TODO: Implement PDF export functionality
        throw new UnsupportedOperationException("PDF export not yet implemented");
    }
    
    // Company profile management
    @Override
    public CompanyDto updateCompanyLogo(Long companyId, String logoUrl) {
        log.info("Updating logo for company ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        company.setLogoUrl(logoUrl);
        company.setUpdatedBy("SYSTEM");
        
        Company updatedCompany = companyRepository.save(company);
        return convertToDto(updatedCompany);
    }
    
    @Override
    public CompanyDto addCompanyImages(Long companyId, List<String> imageUrls) {
        log.info("Adding {} images for company ID: {}", imageUrls.size(), companyId);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        List<String> existingImages = company.getImageUrls() != null ? 
            new ArrayList<>(company.getImageUrls()) : new ArrayList<>();
        existingImages.addAll(imageUrls);
        company.setImageUrls(existingImages);
        company.setUpdatedBy("SYSTEM");
        
        Company updatedCompany = companyRepository.save(company);
        return convertToDto(updatedCompany);
    }
    
    @Override
    public CompanyDto removeCompanyImage(Long companyId, String imageUrl) {
        log.info("Removing image for company ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        if (company.getImageUrls() != null) {
            company.getImageUrls().remove(imageUrl);
            company.setUpdatedBy("SYSTEM");
        }
        
        Company updatedCompany = companyRepository.save(company);
        return convertToDto(updatedCompany);
    }
    
    @Override
    public CompanyDto updateBankingInfo(Long companyId, String bankName, String accountNumber, 
                                       String ifscCode, String accountHolderName) {
        log.info("Updating banking info for company ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + companyId));
        
        company.setBankName(bankName);
        company.setAccountNumber(accountNumber);
        company.setIfscCode(ifscCode);
        company.setAccountHolderName(accountHolderName);
        company.setUpdatedBy("SYSTEM");
        
        Company updatedCompany = companyRepository.save(company);
        return convertToDto(updatedCompany);
    }
    
    // Helper methods
    private void validateUniqueFields(CreateCompanyDto createCompanyDto) {
        if (!isGstNumberUnique(createCompanyDto.getGstNumber())) {
            throw new IllegalArgumentException("GST number already exists: " + createCompanyDto.getGstNumber());
        }
        if (!isPanNumberUnique(createCompanyDto.getPanNumber())) {
            throw new IllegalArgumentException("PAN number already exists: " + createCompanyDto.getPanNumber());
        }
        if (!isCinNumberUnique(createCompanyDto.getCinNumber())) {
            throw new IllegalArgumentException("CIN number already exists: " + createCompanyDto.getCinNumber());
        }
        if (!isEmailUnique(createCompanyDto.getPrimaryEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createCompanyDto.getPrimaryEmail());
        }
    }
    
    private void validateUniqueFieldsForUpdate(Company existingCompany, UpdateCompanyDto updateDto) {
        if (updateDto.getGstNumber() != null && 
            !updateDto.getGstNumber().equals(existingCompany.getGstNumber()) && 
            !isGstNumberUnique(updateDto.getGstNumber())) {
            throw new IllegalArgumentException("GST number already exists: " + updateDto.getGstNumber());
        }
        if (updateDto.getPanNumber() != null && 
            !updateDto.getPanNumber().equals(existingCompany.getPanNumber()) && 
            !isPanNumberUnique(updateDto.getPanNumber())) {
            throw new IllegalArgumentException("PAN number already exists: " + updateDto.getPanNumber());
        }
        if (updateDto.getCinNumber() != null && 
            !updateDto.getCinNumber().equals(existingCompany.getCinNumber()) && 
            !isCinNumberUnique(updateDto.getCinNumber())) {
            throw new IllegalArgumentException("CIN number already exists: " + updateDto.getCinNumber());
        }
        if (updateDto.getPrimaryEmail() != null && 
            !updateDto.getPrimaryEmail().equals(existingCompany.getPrimaryEmail()) && 
            !isEmailUnique(updateDto.getPrimaryEmail())) {
            throw new IllegalArgumentException("Email already exists: " + updateDto.getPrimaryEmail());
        }
    }
    
    private void updateCompanyFields(Company existingCompany, UpdateCompanyDto updateDto) {
        if (updateDto.getName() != null) existingCompany.setName(updateDto.getName());
        if (updateDto.getLegalName() != null) existingCompany.setLegalName(updateDto.getLegalName());
        if (updateDto.getGstNumber() != null) existingCompany.setGstNumber(updateDto.getGstNumber());
        if (updateDto.getPanNumber() != null) existingCompany.setPanNumber(updateDto.getPanNumber());
        if (updateDto.getCinNumber() != null) existingCompany.setCinNumber(updateDto.getCinNumber());
        if (updateDto.getCompanyType() != null) existingCompany.setCompanyType(updateDto.getCompanyType());
        if (updateDto.getBusinessCategory() != null) existingCompany.setBusinessCategory(updateDto.getBusinessCategory());
        if (updateDto.getEstablishedYear() != null) existingCompany.setEstablishedYear(updateDto.getEstablishedYear());
        if (updateDto.getEmployeeCount() != null) existingCompany.setEmployeeCount(updateDto.getEmployeeCount());
        if (updateDto.getAnnualTurnover() != null) existingCompany.setAnnualTurnover(updateDto.getAnnualTurnover());
        if (updateDto.getDescription() != null) existingCompany.setDescription(updateDto.getDescription());
        if (updateDto.getWebsiteUrl() != null) existingCompany.setWebsiteUrl(updateDto.getWebsiteUrl());
        if (updateDto.getPrimaryEmail() != null) existingCompany.setPrimaryEmail(updateDto.getPrimaryEmail());
        if (updateDto.getPrimaryPhone() != null) existingCompany.setPrimaryPhone(updateDto.getPrimaryPhone());
        if (updateDto.getSecondaryPhone() != null) existingCompany.setSecondaryPhone(updateDto.getSecondaryPhone());
        if (updateDto.getAddressLine1() != null) existingCompany.setAddressLine1(updateDto.getAddressLine1());
        if (updateDto.getAddressLine2() != null) existingCompany.setAddressLine2(updateDto.getAddressLine2());
        if (updateDto.getCity() != null) existingCompany.setCity(updateDto.getCity());
        if (updateDto.getState() != null) existingCompany.setState(updateDto.getState());
        if (updateDto.getPostalCode() != null) existingCompany.setPostalCode(updateDto.getPostalCode());
        if (updateDto.getCountry() != null) existingCompany.setCountry(updateDto.getCountry());
        if (updateDto.getIndustries() != null) existingCompany.setIndustries(updateDto.getIndustries());
        if (updateDto.getCertifications() != null) existingCompany.setCertifications(updateDto.getCertifications());
        if (updateDto.getLinkedinUrl() != null) existingCompany.setLinkedinUrl(updateDto.getLinkedinUrl());
        if (updateDto.getFacebookUrl() != null) existingCompany.setFacebookUrl(updateDto.getFacebookUrl());
        if (updateDto.getTwitterUrl() != null) existingCompany.setTwitterUrl(updateDto.getTwitterUrl());
        if (updateDto.getInstagramUrl() != null) existingCompany.setInstagramUrl(updateDto.getInstagramUrl());
        if (updateDto.getLogoUrl() != null) existingCompany.setLogoUrl(updateDto.getLogoUrl());
        if (updateDto.getImageUrls() != null) existingCompany.setImageUrls(updateDto.getImageUrls());
        if (updateDto.getBusinessHours() != null) existingCompany.setBusinessHours(updateDto.getBusinessHours());
        if (updateDto.getWorkingDays() != null) existingCompany.setWorkingDays(updateDto.getWorkingDays());
        if (updateDto.getBankName() != null) existingCompany.setBankName(updateDto.getBankName());
        if (updateDto.getAccountNumber() != null) existingCompany.setAccountNumber(updateDto.getAccountNumber());
        if (updateDto.getIfscCode() != null) existingCompany.setIfscCode(updateDto.getIfscCode());
        if (updateDto.getAccountHolderName() != null) existingCompany.setAccountHolderName(updateDto.getAccountHolderName());
        if (updateDto.getStatus() != null) existingCompany.setStatus(updateDto.getStatus());
    }
    
    private CompanyDto convertToDto(Company company) {
        CompanyDto dto = new CompanyDto();
        BeanUtils.copyProperties(company, dto);
        return dto;
    }
}


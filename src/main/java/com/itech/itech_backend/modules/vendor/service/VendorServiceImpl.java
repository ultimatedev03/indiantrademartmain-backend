package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.enums.VendorBusinessType;
import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.company.repository.CompanyRepository;
import com.itech.itech_backend.modules.vendor.dto.*;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import com.itech.itech_backend.modules.vendor.repository.VendorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VendorServiceImpl implements VendorService {
    
    private final VendorRepository vendorRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    
    // CRUD Operations
    @Override
    public VendorDto createVendor(CreateVendorDto createVendorDto) {
        log.info("Creating new vendor: {}", createVendorDto.getVendorName());
        
        // Validate unique fields
        validateUniqueFields(createVendorDto);
        
        Vendor vendor = new Vendor();
        BeanUtils.copyProperties(createVendorDto, vendor);
        
        // TODO: Password handling should be done at User entity level
        // vendor.setPassword(passwordEncoder.encode(createVendorDto.getPassword()));
        
        // Set default values
        vendor.setVerificationStatus(com.itech.itech_backend.enums.VerificationStatus.PENDING);
        vendor.setCreatedBy("SYSTEM"); // In real implementation, get from SecurityContext
        
        // Link company if provided
        if (createVendorDto.getCompanyId() != null) {
            Company company = companyRepository.findById(createVendorDto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + createVendorDto.getCompanyId()));
            vendor.setCompany(company);
        }
        
        Vendor savedVendor = vendorRepository.save(vendor);
        log.info("Vendor created successfully with ID: {}", savedVendor.getId());
        
        return convertToDto(savedVendor);
    }
    
    @Override
    @Transactional(readOnly = true)
    public VendorDto getVendorById(Long vendorId) {
        log.debug("Fetching vendor with ID: {}", vendorId);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        return convertToDto(vendor);
    }
    
    @Override
    public VendorDto updateVendor(Long vendorId, UpdateVendorDto updateVendorDto) {
        log.info("Updating vendor with ID: {}", vendorId);
        
        Vendor existingVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        // Validate unique fields for update
        validateUniqueFieldsForUpdate(existingVendor, updateVendorDto);
        
        // Update only non-null fields
        updateVendorFields(existingVendor, updateVendorDto);
        existingVendor.setUpdatedBy("SYSTEM");
        
        Vendor updatedVendor = vendorRepository.save(existingVendor);
        log.info("Vendor updated successfully with ID: {}", updatedVendor.getId());
        
        return convertToDto(updatedVendor);
    }
    
    @Override
    public void deleteVendor(Long vendorId) {
        log.info("Deleting vendor with ID: {}", vendorId);
        
        if (!vendorRepository.existsById(vendorId)) {
            throw new EntityNotFoundException("Vendor not found with ID: " + vendorId);
        }
        
        vendorRepository.deleteById(vendorId);
        log.info("Vendor deleted successfully with ID: {}", vendorId);
    }
    
    // Vendor listing and search
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getAllVendors(Pageable pageable) {
        log.debug("Fetching all vendors with pagination");
        
        Page<Vendor> vendors = vendorRepository.findAll(pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> searchVendors(String searchTerm, Pageable pageable) {
        log.debug("Searching vendors with term: {}", searchTerm);
        
        Page<Vendor> vendors = vendorRepository.searchVendors(searchTerm, pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsWithFilters(String vendorName, com.itech.itech_backend.enums.VendorBusinessType businessType,
                                                com.itech.itech_backend.enums.VerificationStatus verificationStatus, Boolean isActive,
                                                Boolean isVerified, Boolean kycApproved,
                                                BigDecimal minRating, Boolean deliveryAvailable,
                                                Boolean installationService, Pageable pageable) {
        log.debug("Fetching vendors with filters");
        
        Page<Vendor> vendors = vendorRepository.findVendorsWithFilters(
            vendorName, businessType, verificationStatus, isActive, isVerified, kycApproved,
            minRating, deliveryAvailable, installationService, pageable
        );
        return vendors.map(this::convertToDto);
    }
    
    // Vendor verification and KYC
    @Override
    public VendorDto verifyVendor(VendorVerificationDto verificationDto) {
        log.info("Processing vendor verification for ID: {}", verificationDto.getVendorId());
        
        Vendor vendor = vendorRepository.findById(verificationDto.getVendorId())
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + verificationDto.getVendorId()));
        
        // TODO: Map VendorStatus to VerificationStatus
        // vendor.setVerificationStatus(mapToVerificationStatus(verificationDto.getVendorStatus()));
        vendor.setKycApprovedBy(verificationDto.getVerifiedBy());
        
        if (verificationDto.getKycApproved() != null) {
            vendor.setKycApproved(verificationDto.getKycApproved());
            if (verificationDto.getKycApproved()) {
                vendor.setKycApprovedAt(LocalDateTime.now());
                vendor.setIsVerified(true);
            } else {
                vendor.setKycRejectionReason(verificationDto.getRejectionReason());
            }
        }
        
        // TODO: Handle verification status mapping
        // if (verificationDto.getVendorStatus() == Vendor.VendorStatus.APPROVED) {
        //     vendor.setIsActive(true);
        //     vendor.setIsVerified(true);
        // } else if (verificationDto.getVendorStatus() == Vendor.VendorStatus.REJECTED) {
        //     vendor.setIsActive(false);
        //     vendor.setKycRejectionReason(verificationDto.getRejectionReason());
        // }
        
        Vendor verifiedVendor = vendorRepository.save(vendor);
        log.info("Vendor verification completed for ID: {}", verifiedVendor.getId());
        
        return convertToDto(verifiedVendor);
    }
    
    @Override
    public VendorDto submitKyc(Long vendorId, List<String> documentUrls) {
        log.info("Submitting KYC for vendor ID: {}", vendorId);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setKycSubmitted(true);
        vendor.setKycSubmittedAt(LocalDateTime.now());
        vendor.setDocumentUrls(documentUrls);
        vendor.setVerificationStatus(com.itech.itech_backend.enums.VerificationStatus.PENDING);
        
        Vendor updatedVendor = vendorRepository.save(vendor);
        log.info("KYC submitted successfully for vendor ID: {}", vendorId);
        
        return convertToDto(updatedVendor);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getPendingKycVendors(Pageable pageable) {
        log.debug("Fetching pending KYC vendors");
        
        Page<Vendor> vendors = vendorRepository.findPendingKycVendors(pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getPendingApprovalVendors(Pageable pageable) {
        log.debug("Fetching pending approval vendors");
        
        Page<Vendor> vendors = vendorRepository.findPendingApprovalVendors(pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByStatus(com.itech.itech_backend.enums.VerificationStatus status, Pageable pageable) {
        log.debug("Fetching vendors by status: {}", status);
        
        Page<Vendor> vendors = vendorRepository.findByVerificationStatus(status, pageable);
        return vendors.map(this::convertToDto);
    }
    
    // Vendor status management
    @Override
    public VendorDto updateVendorStatus(Long vendorId, com.itech.itech_backend.enums.VerificationStatus status) {
        log.info("Updating vendor status for ID: {} to {}", vendorId, status);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        // TODO: Map VendorStatus to VerificationStatus
        // vendor.setVerificationStatus(mapToVerificationStatus(status));
        // For now, just update isActive and isVerified based on status
        vendor.setIsActive(true); // Default to active for compilation
        vendor.setUpdatedBy("SYSTEM");
        
        Vendor updatedVendor = vendorRepository.save(vendor);
        log.info("Vendor status updated successfully for ID: {}", updatedVendor.getId());
        
        return convertToDto(updatedVendor);
    }
    
    @Override
    public VendorDto activateVendor(Long vendorId) {
        log.info("Activating vendor ID: {}", vendorId);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setIsActive(true);
        // TODO: Handle verification status update
        // if (vendor.getVerificationStatus() == VerificationStatus.REJECTED) {
        //     vendor.setVerificationStatus(VerificationStatus.APPROVED);
        // }
        vendor.setUpdatedBy("SYSTEM");
        
        Vendor activatedVendor = vendorRepository.save(vendor);
        return convertToDto(activatedVendor);
    }
    
    @Override
    public VendorDto deactivateVendor(Long vendorId) {
        log.info("Deactivating vendor ID: {}", vendorId);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setIsActive(false);
        vendor.setVerificationStatus(com.itech.itech_backend.enums.VerificationStatus.REJECTED);
        vendor.setUpdatedBy("SYSTEM");
        
        Vendor deactivatedVendor = vendorRepository.save(vendor);
        return convertToDto(deactivatedVendor);
    }
    
    @Override
    public VendorDto suspendVendor(Long vendorId, String reason) {
        log.info("Suspending vendor ID: {} with reason: {}", vendorId, reason);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setIsActive(false);
        vendor.setVerificationStatus(com.itech.itech_backend.enums.VerificationStatus.SUSPENDED);
        vendor.setKycRejectionReason(reason); // Using this field for suspension reason
        vendor.setUpdatedBy("SYSTEM");
        
        Vendor suspendedVendor = vendorRepository.save(vendor);
        return convertToDto(suspendedVendor);
    }
    
    // Vendor type and subscription management
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByType(com.itech.itech_backend.enums.VendorBusinessType businessType, Pageable pageable) {
        log.debug("Fetching vendors by type: {}", businessType);
        
        Page<Vendor> vendors = vendorRepository.findByBusinessType(businessType, pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    public VendorDto upgradeVendorType(Long vendorId, com.itech.itech_backend.enums.VendorBusinessType businessType) {
        log.info("Upgrading vendor type for ID: {} to {}", vendorId, businessType);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        // TODO: Handle vendorType field - not present in current Vendor entity
        // vendor.setVendorType(vendorType);
        
        // Set subscription dates for premium types
        // if (businessType != com.itech.itech_backend.enums.VendorBusinessType.BASIC) {
        //     vendor.setSubscriptionStartDate(LocalDateTime.now());
        //     vendor.setSubscriptionEndDate(LocalDateTime.now().plusMonths(12)); // 1 year subscription
        // }
        
        vendor.setUpdatedBy("SYSTEM");
        
        Vendor upgradedVendor = vendorRepository.save(vendor);
        return convertToDto(upgradedVendor);
    }
    
    @Override
    public VendorDto setFeaturedVendor(Long vendorId, Boolean featured) {
        log.info("Setting featured vendor status for ID: {} to {}", vendorId, featured);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setFeaturedVendor(featured);
        vendor.setUpdatedBy("SYSTEM");
        
        Vendor updatedVendor = vendorRepository.save(vendor);
        return convertToDto(updatedVendor);
    }
    
    @Override
    public VendorDto setPriorityListing(Long vendorId, Boolean priority) {
        log.info("Setting priority listing for vendor ID: {} to {}", vendorId, priority);
        
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setPriorityListing(priority);
        vendor.setUpdatedBy("SYSTEM");
        
        Vendor updatedVendor = vendorRepository.save(vendor);
        return convertToDto(updatedVendor);
    }
    
    // Business category and specialization
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByCategory(Vendor.BusinessCategory category, Pageable pageable) {
        log.debug("Fetching vendors by category: {}", category);
        
        Page<Vendor> vendors = vendorRepository.findByCategory(category, pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByServiceArea(String area, Pageable pageable) {
        log.debug("Fetching vendors by service area: {}", area);
        
        Page<Vendor> vendors = vendorRepository.findByServiceArea(area, pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VendorDto> getVendorsByFeature(String feature, Boolean value) {
        log.debug("Fetching vendors by feature: {} = {}", feature, value);
        
        List<Vendor> vendors = switch (feature.toLowerCase()) {
            case "delivery" -> vendorRepository.findByDeliveryAvailable(value);
            case "installation" -> vendorRepository.findByInstallationService(value);
            case "aftersales" -> vendorRepository.findByAfterSalesSupport(value);
            case "featured" -> vendorRepository.findByFeaturedVendor(value);
            case "priority" -> vendorRepository.findByPriorityListing(value);
            default -> new ArrayList<>();
        };
        
        return vendors.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Performance and analytics
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getTopPerformingVendors(Pageable pageable) {
        log.debug("Fetching top performing vendors");
        
        Page<Vendor> vendors = vendorRepository.findTopPerformingVendors(pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByRating(BigDecimal minRating, Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findByMinimumRating(minRating, pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByOrders(Long minOrders, Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findByMinimumOrders(minOrders, pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByRevenue(BigDecimal minRevenue, Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findByMinimumRevenue(minRevenue, pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getFeaturedVendors(Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findFeaturedVendors(pageable);
        return vendors.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getCertifiedVendors(Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findCertifiedVendors(pageable);
        return vendors.map(this::convertToDto);
    }
    
    // Vendor profile management
    @Override
    public VendorDto updateVendorProfile(Long vendorId, String displayName, String description) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        if (displayName != null) vendor.setDisplayName(displayName);
        if (description != null) vendor.setDescription(description);
        vendor.setUpdatedBy("SYSTEM");
        
        return convertToDto(vendorRepository.save(vendor));
    }
    
    @Override
    public VendorDto updateProfileImages(Long vendorId, String profileImageUrl, String coverImageUrl) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        if (profileImageUrl != null) vendor.setProfileImageUrl(profileImageUrl);
        if (coverImageUrl != null) vendor.setCoverImageUrl(coverImageUrl);
        vendor.setUpdatedBy("SYSTEM");
        
        return convertToDto(vendorRepository.save(vendor));
    }
    
    @Override
    public VendorDto updateContactPerson(Long vendorId, String name, String designation, String phone, String email) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        if (name != null) vendor.setContactPersonName(name);
        if (designation != null) vendor.setContactPersonDesignation(designation);
        if (phone != null) vendor.setContactPersonPhone(phone);
        if (email != null) vendor.setContactPersonEmail(email);
        vendor.setUpdatedBy("SYSTEM");
        
        return convertToDto(vendorRepository.save(vendor));
    }
    
    @Override
    public VendorDto updateBusinessInfo(Long vendorId, Integer establishedYear, String businessType,
                                       List<Vendor.BusinessCategory> categories, List<String> specializations) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        if (establishedYear != null) vendor.setEstablishedYear(establishedYear);
        if (businessType != null) {
            // TODO: Convert String to VendorBusinessType enum
            try {
                vendor.setBusinessType(com.itech.itech_backend.enums.VendorBusinessType.valueOf(businessType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid business type: " + businessType);
            }
        }
        if (categories != null) vendor.setCategories(categories);
        if (specializations != null) vendor.setSpecializations(specializations);
        vendor.setUpdatedBy("SYSTEM");
        
        return convertToDto(vendorRepository.save(vendor));
    }
    
    @Override
    public VendorDto updateServiceAreas(Long vendorId, List<String> serviceAreas) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setServiceAreas(serviceAreas);
        vendor.setUpdatedBy("SYSTEM");
        
        return convertToDto(vendorRepository.save(vendor));
    }
    
    @Override
    public VendorDto updatePaymentMethods(Long vendorId, List<Vendor.PaymentMethod> paymentMethods) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        vendor.setAcceptedPaymentMethods(paymentMethods);
        vendor.setUpdatedBy("SYSTEM");
        
        return convertToDto(vendorRepository.save(vendor));
    }
    
    @Override
    public VendorDto updateCertifications(Long vendorId, List<String> certifications, Boolean isoCertified, Boolean qualityAssured) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
        
        if (certifications != null) vendor.setCertifications(certifications);
        if (isoCertified != null) vendor.setIsoCertified(isoCertified);
        if (qualityAssured != null) vendor.setQualityAssured(qualityAssured);
        vendor.setUpdatedBy("SYSTEM");
        
        return convertToDto(vendorRepository.save(vendor));
    }
    
    // Authentication and validation
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailUnique(String email) {
        return !vendorRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneUnique(String phone) {
        return !vendorRepository.existsByPhone(phone);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isCompanyLinked(Long companyId) {
        return vendorRepository.existsByCompanyId(companyId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public VendorDto getVendorByEmail(String email) {
        Vendor vendor = vendorRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with email: " + email));
        return convertToDto(vendor);
    }
    
    @Override
    @Transactional(readOnly = true)
    public VendorDto getVendorByPhone(String phone) {
        Vendor vendor = vendorRepository.findByPhone(phone)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with phone: " + phone));
        return convertToDto(vendor);
    }
    
    @Override
    @Transactional(readOnly = true)
    public VendorDto getVendorByCompany(Long companyId) {
        Vendor vendor = vendorRepository.findByCompanyId(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with company ID: " + companyId));
        return convertToDto(vendor);
    }
    
    @Override
    public VendorDto authenticateVendor(String email, String password) {
        Vendor vendor = vendorRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Vendor not found with email: " + email));
        
        // TODO: Authentication should be handled at User entity level
        // if (!passwordEncoder.matches(password, vendor.getPassword())) {
        //     throw new IllegalArgumentException("Invalid credentials");
        // }
        
        // For now, skip authentication and just update last login
        vendor.setLastLogin(LocalDateTime.now());
        vendorRepository.save(vendor);
        
        return convertToDto(vendor);
    }
    
    @Override
    public VendorDto updatePassword(Long vendorId, String currentPassword, String newPassword) {
        // TODO: Password change should be handled at User entity level
        throw new UnsupportedOperationException("Password change should be handled at User entity level");
    }
    
    // Statistics and analytics
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getVendorStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalVendors", vendorRepository.count());
        stats.put("activeVendors", vendorRepository.countActiveVendors());
        stats.put("verifiedVendors", vendorRepository.countVerifiedVendors());
        stats.put("kycApprovedVendors", vendorRepository.countKycApprovedVendors());
        stats.put("featuredVendors", vendorRepository.countFeaturedVendors());
        stats.put("pendingApproval", vendorRepository.countByVerificationStatus(com.itech.itech_backend.enums.VerificationStatus.PENDING));
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalVendorsCount() {
        return vendorRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getActiveVendorsCount() {
        return vendorRepository.countActiveVendors();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getVerifiedVendorsCount() {
        return vendorRepository.countVerifiedVendors();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getKycApprovedVendorsCount() {
        return vendorRepository.countKycApprovedVendors();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getFeaturedVendorsCount() {
        return vendorRepository.countFeaturedVendors();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageVendorRating() {
        return vendorRepository.getAverageVendorRating();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPlatformRevenue() {
        return vendorRepository.getTotalPlatformRevenue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getTotalOrders() {
        return vendorRepository.getTotalOrders();
    }
    
    // Recent and trending
    @Override
    @Transactional(readOnly = true)
    public Page<VendorDto> getRecentVendors(Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findRecentVendors(pageable);
        return vendors.map(this::convertToDto);
    }
    
    // Placeholder implementations for methods not yet implemented
    @Override
    public VendorDto updateFinancialInfo(Long vendorId, BigDecimal minimumOrderValue, BigDecimal creditLimit, Integer paymentTerms) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateServiceCapabilities(Long vendorId, Boolean delivery, Boolean installation, Boolean afterSales) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Page<VendorDto> getActiveSubscriptionVendors(Pageable pageable) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public List<VendorDto> getExpiringSubscriptions(int daysAhead) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto renewSubscription(Long vendorId, int months) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto cancelSubscription(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto uploadDocument(Long vendorId, String documentUrl) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto removeDocument(Long vendorId, String documentUrl) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public List<String> getVendorDocuments(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateNotificationSettings(Long vendorId, Boolean email, Boolean sms) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateBusinessSettings(Long vendorId, Boolean autoApprove, Vendor.CatalogVisibility visibility) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateMarketingContent(Long vendorId, String bannerUrl, String videoUrl, String socialMediaLinks) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateLastLogin(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateLastActivity(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto incrementProfileViews(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto incrementProductViews(Long vendorId, Long views) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto incrementInquiryCount(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateOrderMetrics(Long vendorId, Long totalOrders, Long completedOrders, Long cancelledOrders) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateRevenueMetrics(Long vendorId, BigDecimal revenue) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateRatingMetrics(Long vendorId, BigDecimal rating, Integer totalReviews) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateResponseTime(Long vendorId, Integer hours) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto updateFulfillmentTime(Long vendorId, Integer days) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<VerificationStatus, Long> getVendorCountByStatus() {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<VendorBusinessType, Long> getVendorCountByType() {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<Vendor.BusinessCategory, Long> getVendorCountByCategory() {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public List<VendorDto> getNewRegistrations(int days) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public List<VendorDto> getInactiveVendors(int days) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public List<VendorDto> createVendorsInBulk(List<CreateVendorDto> createVendorDtos) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<String, Object> bulkUpdateVendorStatus(List<Long> vendorIds, VerificationStatus status) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<String, Object> bulkVerifyVendors(List<Long> vendorIds, Boolean approved) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<String, Object> bulkUpdateVendorType(List<Long> vendorIds, VendorBusinessType businessType) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public byte[] exportVendorsToExcel(List<Long> vendorIds) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public byte[] exportVendorsToPdf(List<Long> vendorIds) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto linkCompany(Long vendorId, Long companyId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public VendorDto unlinkCompany(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<String, Object> getVendorDashboardData(Long vendorId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public Map<String, Object> getAdminDashboardData() {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public List<VendorDto> getVendorRecommendations(String area, Vendor.BusinessCategory category, int limit) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    // Helper methods
    private void validateUniqueFields(CreateVendorDto createVendorDto) {
        if (!isEmailUnique(createVendorDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createVendorDto.getEmail());
        }
        if (!isPhoneUnique(createVendorDto.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists: " + createVendorDto.getPhone());
        }
        if (createVendorDto.getCompanyId() != null && isCompanyLinked(createVendorDto.getCompanyId())) {
            throw new IllegalArgumentException("Company already linked to another vendor: " + createVendorDto.getCompanyId());
        }
    }
    
    private void validateUniqueFieldsForUpdate(Vendor existingVendor, UpdateVendorDto updateDto) {
        if (updateDto.getEmail() != null && 
            !updateDto.getEmail().equals(existingVendor.getEmail()) && 
            !isEmailUnique(updateDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + updateDto.getEmail());
        }
        if (updateDto.getPhone() != null && 
            !updateDto.getPhone().equals(existingVendor.getPhone()) && 
            !isPhoneUnique(updateDto.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists: " + updateDto.getPhone());
        }
        if (updateDto.getCompanyId() != null &&
            !Objects.equals(updateDto.getCompanyId(), existingVendor.getCompany() != null ? existingVendor.getCompany().getId() : null) &&
            isCompanyLinked(updateDto.getCompanyId())) {
            throw new IllegalArgumentException("Company already linked to another vendor: " + updateDto.getCompanyId());
        }
    }
    
    private void updateVendorFields(Vendor existingVendor, UpdateVendorDto updateDto) {
        if (updateDto.getVendorName() != null) existingVendor.setBusinessName(updateDto.getVendorName());
        if (updateDto.getEmail() != null) existingVendor.setEmail(updateDto.getEmail());
        if (updateDto.getPhone() != null) existingVendor.setPhone(updateDto.getPhone());
        if (updateDto.getDisplayName() != null) existingVendor.setDisplayName(updateDto.getDisplayName());
        if (updateDto.getDescription() != null) existingVendor.setDescription(updateDto.getDescription());
        if (updateDto.getProfileImageUrl() != null) existingVendor.setProfileImageUrl(updateDto.getProfileImageUrl());
        if (updateDto.getCoverImageUrl() != null) existingVendor.setCoverImageUrl(updateDto.getCoverImageUrl());
        if (updateDto.getContactPersonName() != null) existingVendor.setContactPersonName(updateDto.getContactPersonName());
        if (updateDto.getContactPersonDesignation() != null) existingVendor.setContactPersonDesignation(updateDto.getContactPersonDesignation());
        if (updateDto.getContactPersonPhone() != null) existingVendor.setContactPersonPhone(updateDto.getContactPersonPhone());
        if (updateDto.getContactPersonEmail() != null) existingVendor.setContactPersonEmail(updateDto.getContactPersonEmail());
        if (updateDto.getEstablishedYear() != null) existingVendor.setEstablishedYear(updateDto.getEstablishedYear());
        if (updateDto.getBusinessType() != null) {
            // TODO: Convert String to VendorBusinessType enum
            try {
                existingVendor.setBusinessType(com.itech.itech_backend.enums.VendorBusinessType.valueOf(updateDto.getBusinessType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid business type: " + updateDto.getBusinessType());
            }
        }
        if (updateDto.getCategories() != null) existingVendor.setCategories(updateDto.getCategories());
        if (updateDto.getSpecializations() != null) existingVendor.setSpecializations(updateDto.getSpecializations());
        if (updateDto.getResponseTimeHours() != null) existingVendor.setResponseTimeHours(updateDto.getResponseTimeHours());
        if (updateDto.getFulfillmentTimeDays() != null) existingVendor.setFulfillmentTimeDays(updateDto.getFulfillmentTimeDays());
        if (updateDto.getMinimumOrderValue() != null) existingVendor.setMinimumOrderValue(updateDto.getMinimumOrderValue());
        if (updateDto.getCreditLimit() != null) existingVendor.setCreditLimit(updateDto.getCreditLimit());
        if (updateDto.getPaymentTermsDays() != null) existingVendor.setPaymentTermsDays(updateDto.getPaymentTermsDays());
        if (updateDto.getAcceptedPaymentMethods() != null) existingVendor.setAcceptedPaymentMethods(updateDto.getAcceptedPaymentMethods());
        if (updateDto.getServiceAreas() != null) existingVendor.setServiceAreas(updateDto.getServiceAreas());
        if (updateDto.getDeliveryAvailable() != null) existingVendor.setDeliveryAvailable(updateDto.getDeliveryAvailable());
        if (updateDto.getInstallationService() != null) existingVendor.setInstallationService(updateDto.getInstallationService());
        if (updateDto.getAfterSalesSupport() != null) existingVendor.setAfterSalesSupport(updateDto.getAfterSalesSupport());
        if (updateDto.getCertifications() != null) existingVendor.setCertifications(updateDto.getCertifications());
        if (updateDto.getIsoCertified() != null) existingVendor.setIsoCertified(updateDto.getIsoCertified());
        if (updateDto.getQualityAssured() != null) existingVendor.setQualityAssured(updateDto.getQualityAssured());
        if (updateDto.getDocumentUrls() != null) existingVendor.setDocumentUrls(updateDto.getDocumentUrls());
        if (updateDto.getPromotionalBannerUrl() != null) existingVendor.setPromotionalBannerUrl(updateDto.getPromotionalBannerUrl());
        if (updateDto.getPromotionalVideoUrl() != null) existingVendor.setPromotionalVideoUrl(updateDto.getPromotionalVideoUrl());
        if (updateDto.getSocialMediaLinks() != null) existingVendor.setSocialMediaLinks(updateDto.getSocialMediaLinks());
        if (updateDto.getEmailNotifications() != null) existingVendor.setEmailNotifications(updateDto.getEmailNotifications());
        if (updateDto.getSmsNotifications() != null) existingVendor.setSmsNotifications(updateDto.getSmsNotifications());
        if (updateDto.getAutoApproveOrders() != null) existingVendor.setAutoApproveOrders(updateDto.getAutoApproveOrders());
        if (updateDto.getCatalogVisibility() != null) existingVendor.setCatalogVisibility(updateDto.getCatalogVisibility());
        
        // Admin-only fields - TODO: Handle these fields
        // if (updateDto.getVendorType() != null) existingVendor.setVendorType(updateDto.getVendorType());
        // if (updateDto.getVendorStatus() != null) existingVendor.setVerificationStatus(mapToVerificationStatus(updateDto.getVendorStatus()));
        if (updateDto.getIsActive() != null) existingVendor.setIsActive(updateDto.getIsActive());
        if (updateDto.getFeaturedVendor() != null) existingVendor.setFeaturedVendor(updateDto.getFeaturedVendor());
        if (updateDto.getPriorityListing() != null) existingVendor.setPriorityListing(updateDto.getPriorityListing());
        
        // Link company if provided
        if (updateDto.getCompanyId() != null && !Objects.equals(updateDto.getCompanyId(), existingVendor.getCompany() != null ? existingVendor.getCompany().getId() : null)) {
            Company company = companyRepository.findById(updateDto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + updateDto.getCompanyId()));
            existingVendor.setCompany(company);
        }
    }
    
    private VendorDto convertToDto(Vendor vendor) {
        VendorDto dto = new VendorDto();
        BeanUtils.copyProperties(vendor, dto);
        
        // Set computed fields
        if (vendor.getTotalOrders() != null && vendor.getTotalOrders() > 0) {
            dto.setCompletionRate(vendor.getCompletedOrders().doubleValue() / vendor.getTotalOrders().doubleValue() * 100.0);
            dto.setCancellationRate(vendor.getCancelledOrders().doubleValue() / vendor.getTotalOrders().doubleValue() * 100.0);
        }
        
        dto.setIsSubscriptionActive(vendor.getSubscriptionEndDate() != null && vendor.getSubscriptionEndDate().isAfter(LocalDateTime.now()));
        
        if (vendor.getSubscriptionEndDate() != null) {
            dto.setDaysUntilSubscriptionExpiry((int) ChronoUnit.DAYS.between(LocalDateTime.now(), vendor.getSubscriptionEndDate()));
        }
        
        // TODO: Handle vendorType badge - field not present in current Vendor entity
        dto.setVendorBadge("BASIC"); // Default badge for now
        dto.setIsEligibleForPremium(vendor.getIsVerified() && vendor.getKycApproved() && vendor.getIsActive());
        
        return dto;
    }
}


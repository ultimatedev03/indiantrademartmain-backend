package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.modules.vendor.model.VendorProfile;
import com.itech.itech_backend.modules.vendor.repository.VendorProfileRepository;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.core.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VendorProfileService {

    private final VendorProfileRepository vendorProfileRepository;

    // Create and Update operations
    @Transactional
    public VendorProfile createVendorProfile(VendorProfile vendorProfile) {
        log.info("Creating vendor profile for vendor: {}", vendorProfile.getVendorName());
        
        // Set default values
        if (vendorProfile.getVendorStatus() == null) {
            vendorProfile.setVendorStatus(VendorProfile.VendorStatus.PENDING);
        }
        if (vendorProfile.getVendorType() == null) {
            vendorProfile.setVendorType(VendorProfile.VendorType.BASIC);
        }
        
        return vendorProfileRepository.save(vendorProfile);
    }

    @Transactional
    public VendorProfile updateVendorProfile(Long vendorId, VendorProfile updatedProfile) {
        log.info("Updating vendor profile with ID: {}", vendorId);
        
        VendorProfile existingProfile = getVendorProfileById(vendorId);
        
        // Update allowed fields
        existingProfile.setVendorName(updatedProfile.getVendorName());
        existingProfile.setDisplayName(updatedProfile.getDisplayName());
        existingProfile.setDescription(updatedProfile.getDescription());
        existingProfile.setProfileImageUrl(updatedProfile.getProfileImageUrl());
        existingProfile.setCoverImageUrl(updatedProfile.getCoverImageUrl());
        
        // Contact person details
        existingProfile.setContactPersonName(updatedProfile.getContactPersonName());
        existingProfile.setContactPersonDesignation(updatedProfile.getContactPersonDesignation());
        existingProfile.setContactPersonPhone(updatedProfile.getContactPersonPhone());
        existingProfile.setContactPersonEmail(updatedProfile.getContactPersonEmail());
        
        // Business information
        existingProfile.setBusinessType(updatedProfile.getBusinessType());
        existingProfile.setMinimumOrderValue(updatedProfile.getMinimumOrderValue());
        existingProfile.setPaymentTermsDays(updatedProfile.getPaymentTermsDays());
        
        // Services
        existingProfile.setDeliveryAvailable(updatedProfile.getDeliveryAvailable());
        existingProfile.setInstallationService(updatedProfile.getInstallationService());
        existingProfile.setAfterSalesSupport(updatedProfile.getAfterSalesSupport());
        
        // Settings
        existingProfile.setEmailNotifications(updatedProfile.getEmailNotifications());
        existingProfile.setSmsNotifications(updatedProfile.getSmsNotifications());
        existingProfile.setCatalogVisibility(updatedProfile.getCatalogVisibility());
        
        return vendorProfileRepository.save(existingProfile);
    }

    // Read operations
    public VendorProfile getVendorProfileById(Long vendorId) {
        return vendorProfileRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor profile not found with ID: " + vendorId));
    }

    public Optional<VendorProfile> findByVendorName(String vendorName) {
        return vendorProfileRepository.findByVendorName(vendorName);
    }

    public Optional<VendorProfile> findByCompanyId(Long companyId) {
        return vendorProfileRepository.findByCompanyId(companyId);
    }

    public Optional<VendorProfile> findByUserId(Long userId) {
        return vendorProfileRepository.findByUserId(userId);
    }

    public Page<VendorProfile> searchVendors(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return vendorProfileRepository.findByVendorStatusAndIsActive(
                VendorProfile.VendorStatus.APPROVED, true, pageable);
        }
        return vendorProfileRepository.searchVendors(keyword, pageable);
    }

    public Page<VendorProfile> getVendorsByStatus(VendorProfile.VendorStatus status, Boolean isActive, Pageable pageable) {
        return vendorProfileRepository.findByVendorStatusAndIsActive(status, isActive, pageable);
    }

    public Page<VendorProfile> getVendorsByLocation(String city, String state, Pageable pageable) {
        if (city != null && !city.isEmpty()) {
            return vendorProfileRepository.findByCityAndStatus(city, VendorProfile.VendorStatus.APPROVED, pageable);
        } else if (state != null && !state.isEmpty()) {
            return vendorProfileRepository.findByStateAndStatus(state, VendorProfile.VendorStatus.APPROVED, pageable);
        }
        return Page.empty();
    }

    public Page<VendorProfile> getTopRatedVendors(Pageable pageable) {
        return vendorProfileRepository.findTopRatedVendors(VendorProfile.VendorStatus.APPROVED, pageable);
    }

    public Page<VendorProfile> getTopVendorsByRevenue(Pageable pageable) {
        return vendorProfileRepository.findTopVendorsByRevenue(VendorProfile.VendorStatus.APPROVED, pageable);
    }

    public List<VendorProfile> getFeaturedVendors() {
        return vendorProfileRepository.findByFeaturedVendorTrue();
    }

    public Page<VendorProfile> getVendorsByMinRating(BigDecimal minRating, Pageable pageable) {
        return vendorProfileRepository.findByMinimumRating(minRating, pageable);
    }

    // Admin operations
    @Transactional
    public VendorProfile approveVendor(Long vendorId, String approvedBy) {
        log.info("Approving vendor with ID: {} by: {}", vendorId, approvedBy);
        
        VendorProfile vendor = getVendorProfileById(vendorId);
        vendor.setVendorStatus(VendorProfile.VendorStatus.APPROVED);
        vendor.setKycApproved(true);
        vendor.setKycApprovedAt(LocalDateTime.now());
        vendor.setKycApprovedBy(approvedBy);
        
        return vendorProfileRepository.save(vendor);
    }

    @Transactional
    public VendorProfile rejectVendor(Long vendorId, String reason, String rejectedBy) {
        log.info("Rejecting vendor with ID: {} by: {}", vendorId, rejectedBy);
        
        VendorProfile vendor = getVendorProfileById(vendorId);
        vendor.setVendorStatus(VendorProfile.VendorStatus.REJECTED);
        vendor.setKycApproved(false);
        vendor.setKycRejectionReason(reason);
        vendor.setKycApprovedBy(rejectedBy);
        
        return vendorProfileRepository.save(vendor);
    }

    @Transactional
    public VendorProfile suspendVendor(Long vendorId, String reason) {
        log.info("Suspending vendor with ID: {}", vendorId);
        
        VendorProfile vendor = getVendorProfileById(vendorId);
        vendor.setVendorStatus(VendorProfile.VendorStatus.SUSPENDED);
        vendor.setIsActive(false);
        vendor.setKycRejectionReason(reason);
        
        return vendorProfileRepository.save(vendor);
    }

    // Analytics operations
    public Long getTotalVendorCount() {
        return vendorProfileRepository.count();
    }

    public Long getVerifiedVendorCount() {
        return vendorProfileRepository.countVerifiedVendors();
    }

    public Long getVendorCountByStatus(VendorProfile.VendorStatus status) {
        return vendorProfileRepository.countByStatus(status);
    }

    public Optional<BigDecimal> getAverageVendorRating() {
        return vendorProfileRepository.getAverageRating();
    }

    public List<VendorProfile> getPendingVerifications() {
        return vendorProfileRepository.findByKycSubmittedTrueAndKycApprovedFalse();
    }

    // Profile activity operations
    @Transactional
    public void incrementProfileViews(Long vendorId) {
        vendorProfileRepository.incrementProfileViews(vendorId);
    }

    @Transactional
    public void incrementInquiryCount(Long vendorId) {
        vendorProfileRepository.incrementInquiryCount(vendorId);
    }

    @Transactional
    public void updateLastActivity(Long vendorId) {
        VendorProfile vendor = getVendorProfileById(vendorId);
        vendor.updateLastActivity();
        vendorProfileRepository.save(vendor);
    }

    // Premium features
    @Transactional
    public VendorProfile setFeaturedStatus(Long vendorId, boolean featured) {
        log.info("Setting featured status for vendor {}: {}", vendorId, featured);
        
        VendorProfile vendor = getVendorProfileById(vendorId);
        vendor.setFeaturedVendor(featured);
        
        return vendorProfileRepository.save(vendor);
    }

    @Transactional
    public VendorProfile upgradeVendorType(Long vendorId, VendorProfile.VendorType newType) {
        log.info("Upgrading vendor {} to type: {}", vendorId, newType);
        
        VendorProfile vendor = getVendorProfileById(vendorId);
        vendor.setVendorType(newType);
        
        // Set premium features based on type
        if (newType == VendorProfile.VendorType.PREMIUM || newType == VendorProfile.VendorType.ENTERPRISE) {
            vendor.setPriorityListing(true);
        }
        
        return vendorProfileRepository.save(vendor);
    }

    // Business logic validations
    public boolean canVendorReceiveInquiries(Long vendorId) {
        VendorProfile vendor = getVendorProfileById(vendorId);
        return vendor.isApprovedVendor() && vendor.getIsActive();
    }

    public boolean isVendorEligibleForPremium(Long vendorId) {
        VendorProfile vendor = getVendorProfileById(vendorId);
        return vendor.isVerifiedVendor() && 
               vendor.getTotalOrders() > 10 && 
               vendor.getAverageRating().compareTo(BigDecimal.valueOf(4.0)) >= 0;
    }
}

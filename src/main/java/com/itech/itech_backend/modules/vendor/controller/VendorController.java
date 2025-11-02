package com.itech.itech_backend.modules.vendor.controller;

import com.itech.itech_backend.enums.VendorType;
import com.itech.itech_backend.modules.vendor.dto.*;
import com.itech.itech_backend.modules.vendor.model.Vendor;
import com.itech.itech_backend.modules.vendor.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
public class VendorController {
    
    private final VendorService vendorService;
    
    // CRUD Operations
    @PostMapping
    public ResponseEntity<VendorDto> createVendor(@Valid @RequestBody CreateVendorDto createVendorDto) {
        log.info("REST request to create vendor: {}", createVendorDto.getVendorName());
        VendorDto vendorDto = vendorService.createVendor(createVendorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vendorDto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> getVendorById(@PathVariable Long id) {
        log.debug("REST request to get vendor by ID: {}", id);
        VendorDto vendorDto = vendorService.getVendorById(id);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VendorDto> updateVendor(@PathVariable Long id, 
                                                 @Valid @RequestBody UpdateVendorDto updateVendorDto) {
        log.info("REST request to update vendor with ID: {}", id);
        VendorDto vendorDto = vendorService.updateVendor(id, updateVendorDto);
        return ResponseEntity.ok(vendorDto);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        log.info("REST request to delete vendor with ID: {}", id);
        vendorService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }
    
    // Vendor listing and search
    @GetMapping
    public ResponseEntity<Page<VendorDto>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to get all vendors - page: {}, size: {}", page, size);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<VendorDto> vendors = vendorService.getAllVendors(pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<VendorDto>> searchVendors(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vendorName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to search vendors with term: {}", searchTerm);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<VendorDto> vendors = vendorService.searchVendors(searchTerm, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<Page<VendorDto>> getVendorsWithFilters(
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) com.itech.itech_backend.enums.VendorBusinessType businessType,
            @RequestParam(required = false) com.itech.itech_backend.enums.VerificationStatus verificationStatus,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) Boolean kycApproved,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean deliveryAvailable,
            @RequestParam(required = false) Boolean installationService,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vendorName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to get vendors with filters");
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<VendorDto> vendors = vendorService.getVendorsWithFilters(
            vendorName, businessType, verificationStatus, isActive, isVerified, kycApproved,
            minRating, deliveryAvailable, installationService, pageRequest
        );
        return ResponseEntity.ok(vendors);
    }
    
    // Vendor verification and KYC
    @PostMapping("/verify")
    public ResponseEntity<VendorDto> verifyVendor(@Valid @RequestBody VendorVerificationDto verificationDto) {
        log.info("REST request to verify vendor with ID: {}", verificationDto.getVendorId());
        VendorDto vendorDto = vendorService.verifyVendor(verificationDto);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PostMapping("/{id}/kyc")
    public ResponseEntity<VendorDto> submitKyc(@PathVariable Long id, @RequestBody List<String> documentUrls) {
        log.info("REST request to submit KYC for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.submitKyc(id, documentUrls);
        return ResponseEntity.ok(vendorDto);
    }
    
    @GetMapping("/kyc/pending")
    public ResponseEntity<Page<VendorDto>> getPendingKycVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get pending KYC vendors");
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "kycSubmittedAt"));
        Page<VendorDto> vendors = vendorService.getPendingKycVendors(pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/approval/pending")
    public ResponseEntity<Page<VendorDto>> getPendingApprovalVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get pending approval vendors");
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<VendorDto> vendors = vendorService.getPendingApprovalVendors(pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<VendorDto>> getVendorsByStatus(
            @PathVariable com.itech.itech_backend.enums.VerificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get vendors by status: {}", status);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<VendorDto> vendors = vendorService.getVendorsByStatus(status, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    // Vendor status management
    @PatchMapping("/{id}/status")
    public ResponseEntity<VendorDto> updateVendorStatus(@PathVariable Long id, 
                                                       @RequestParam com.itech.itech_backend.enums.VerificationStatus status) {
        log.info("REST request to update vendor status for ID: {} to {}", id, status);
        VendorDto vendorDto = vendorService.updateVendorStatus(id, status);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<VendorDto> activateVendor(@PathVariable Long id) {
        log.info("REST request to activate vendor ID: {}", id);
        VendorDto vendorDto = vendorService.activateVendor(id);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<VendorDto> deactivateVendor(@PathVariable Long id) {
        log.info("REST request to deactivate vendor ID: {}", id);
        VendorDto vendorDto = vendorService.deactivateVendor(id);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PostMapping("/{id}/suspend")
    public ResponseEntity<VendorDto> suspendVendor(@PathVariable Long id, @RequestParam String reason) {
        log.info("REST request to suspend vendor ID: {} with reason: {}", id, reason);
        VendorDto vendorDto = vendorService.suspendVendor(id, reason);
        return ResponseEntity.ok(vendorDto);
    }
    
    // Vendor type and subscription management
    @GetMapping("/type/{businessType}")
    public ResponseEntity<Page<VendorDto>> getVendorsByType(
            @PathVariable com.itech.itech_backend.enums.VendorBusinessType businessType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get vendors by type: {}", businessType);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "businessName"));
        Page<VendorDto> vendors = vendorService.getVendorsByType(businessType, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @PostMapping("/{id}/upgrade")
    public ResponseEntity<VendorDto> upgradeVendorType(@PathVariable Long id, 
                                                      @RequestParam com.itech.itech_backend.enums.VendorBusinessType businessType) {
        log.info("REST request to upgrade vendor type for ID: {} to {}", id, businessType);
        VendorDto vendorDto = vendorService.upgradeVendorType(id, businessType);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/featured")
    public ResponseEntity<VendorDto> setFeaturedVendor(@PathVariable Long id, 
                                                      @RequestParam Boolean featured) {
        log.info("REST request to set featured vendor status for ID: {} to {}", id, featured);
        VendorDto vendorDto = vendorService.setFeaturedVendor(id, featured);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/priority")
    public ResponseEntity<VendorDto> setPriorityListing(@PathVariable Long id, 
                                                       @RequestParam Boolean priority) {
        log.info("REST request to set priority listing for vendor ID: {} to {}", id, priority);
        VendorDto vendorDto = vendorService.setPriorityListing(id, priority);
        return ResponseEntity.ok(vendorDto);
    }
    
    // Business category and specialization
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<VendorDto>> getVendorsByCategory(
            @PathVariable Vendor.BusinessCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get vendors by category: {}", category);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "businessName"));
        Page<VendorDto> vendors = vendorService.getVendorsByCategory(category, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/service-area/{area}")
    public ResponseEntity<Page<VendorDto>> getVendorsByServiceArea(
            @PathVariable String area,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get vendors by service area: {}", area);
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "businessName"));
        Page<VendorDto> vendors = vendorService.getVendorsByServiceArea(area, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/feature/{feature}")
    public ResponseEntity<List<VendorDto>> getVendorsByFeature(
            @PathVariable String feature, 
            @RequestParam Boolean value) {
        
        log.debug("REST request to get vendors by feature: {} = {}", feature, value);
        List<VendorDto> vendors = vendorService.getVendorsByFeature(feature, value);
        return ResponseEntity.ok(vendors);
    }
    
    // Performance and analytics
    @GetMapping("/top-performing")
    public ResponseEntity<Page<VendorDto>> getTopPerformingVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.debug("REST request to get top performing vendors");
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<VendorDto> vendors = vendorService.getTopPerformingVendors(pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/rating/{minRating}")
    public ResponseEntity<Page<VendorDto>> getVendorsByRating(
            @PathVariable BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get vendors by minimum rating: {}", minRating);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<VendorDto> vendors = vendorService.getVendorsByRating(minRating, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/orders/{minOrders}")
    public ResponseEntity<Page<VendorDto>> getVendorsByOrders(
            @PathVariable Long minOrders,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get vendors by minimum orders: {}", minOrders);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<VendorDto> vendors = vendorService.getVendorsByOrders(minOrders, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/revenue/{minRevenue}")
    public ResponseEntity<Page<VendorDto>> getVendorsByRevenue(
            @PathVariable BigDecimal minRevenue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get vendors by minimum revenue: {}", minRevenue);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<VendorDto> vendors = vendorService.getVendorsByRevenue(minRevenue, pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<Page<VendorDto>> getFeaturedVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get featured vendors");
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<VendorDto> vendors = vendorService.getFeaturedVendors(pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/certified")
    public ResponseEntity<Page<VendorDto>> getCertifiedVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get certified vendors");
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<VendorDto> vendors = vendorService.getCertifiedVendors(pageRequest);
        return ResponseEntity.ok(vendors);
    }
    
    // Vendor profile management
    @PatchMapping("/{id}/profile")
    public ResponseEntity<VendorDto> updateVendorProfile(@PathVariable Long id,
                                                        @RequestParam(required = false) String displayName,
                                                        @RequestParam(required = false) String description) {
        log.info("REST request to update vendor profile for ID: {}", id);
        VendorDto vendorDto = vendorService.updateVendorProfile(id, displayName, description);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/images")
    public ResponseEntity<VendorDto> updateProfileImages(@PathVariable Long id,
                                                        @RequestParam(required = false) String profileImageUrl,
                                                        @RequestParam(required = false) String coverImageUrl) {
        log.info("REST request to update profile images for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.updateProfileImages(id, profileImageUrl, coverImageUrl);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/contact-person")
    public ResponseEntity<VendorDto> updateContactPerson(@PathVariable Long id,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String designation,
                                                        @RequestParam(required = false) String phone,
                                                        @RequestParam(required = false) String email) {
        log.info("REST request to update contact person for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.updateContactPerson(id, name, designation, phone, email);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/business-info")
    public ResponseEntity<VendorDto> updateBusinessInfo(@PathVariable Long id,
                                                       @RequestParam(required = false) Integer establishedYear,
                                                       @RequestParam(required = false) String businessType,
                                                       @RequestBody(required = false) List<Vendor.BusinessCategory> categories,
                                                       @RequestBody(required = false) List<String> specializations) {
        log.info("REST request to update business info for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.updateBusinessInfo(id, establishedYear, businessType, categories, specializations);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/service-areas")
    public ResponseEntity<VendorDto> updateServiceAreas(@PathVariable Long id,
                                                       @RequestBody List<String> serviceAreas) {
        log.info("REST request to update service areas for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.updateServiceAreas(id, serviceAreas);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/payment-methods")
    public ResponseEntity<VendorDto> updatePaymentMethods(@PathVariable Long id,
                                                         @RequestBody List<Vendor.PaymentMethod> paymentMethods) {
        log.info("REST request to update payment methods for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.updatePaymentMethods(id, paymentMethods);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/certifications")
    public ResponseEntity<VendorDto> updateCertifications(@PathVariable Long id,
                                                         @RequestBody(required = false) List<String> certifications,
                                                         @RequestParam(required = false) Boolean isoCertified,
                                                         @RequestParam(required = false) Boolean qualityAssured) {
        log.info("REST request to update certifications for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.updateCertifications(id, certifications, isoCertified, qualityAssured);
        return ResponseEntity.ok(vendorDto);
    }
    
    // Authentication and validation
    @GetMapping("/validate/email/{email}")
    public ResponseEntity<Boolean> isEmailUnique(@PathVariable String email) {
        log.debug("REST request to validate email uniqueness: {}", email);
        boolean isUnique = vendorService.isEmailUnique(email);
        return ResponseEntity.ok(isUnique);
    }
    
    @GetMapping("/validate/phone/{phone}")
    public ResponseEntity<Boolean> isPhoneUnique(@PathVariable String phone) {
        log.debug("REST request to validate phone uniqueness: {}", phone);
        boolean isUnique = vendorService.isPhoneUnique(phone);
        return ResponseEntity.ok(isUnique);
    }
    
    @GetMapping("/validate/company/{companyId}")
    public ResponseEntity<Boolean> isCompanyLinked(@PathVariable Long companyId) {
        log.debug("REST request to check if company is linked: {}", companyId);
        boolean isLinked = vendorService.isCompanyLinked(companyId);
        return ResponseEntity.ok(isLinked);
    }
    
    @GetMapping("/lookup/email/{email}")
    public ResponseEntity<VendorDto> getVendorByEmail(@PathVariable String email) {
        log.debug("REST request to get vendor by email: {}", email);
        VendorDto vendorDto = vendorService.getVendorByEmail(email);
        return ResponseEntity.ok(vendorDto);
    }
    
    @GetMapping("/lookup/phone/{phone}")
    public ResponseEntity<VendorDto> getVendorByPhone(@PathVariable String phone) {
        log.debug("REST request to get vendor by phone: {}", phone);
        VendorDto vendorDto = vendorService.getVendorByPhone(phone);
        return ResponseEntity.ok(vendorDto);
    }
    
    @GetMapping("/lookup/company/{companyId}")
    public ResponseEntity<VendorDto> getVendorByCompany(@PathVariable Long companyId) {
        log.debug("REST request to get vendor by company ID: {}", companyId);
        VendorDto vendorDto = vendorService.getVendorByCompany(companyId);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<VendorDto> authenticateVendor(@RequestParam String email, 
                                                       @RequestParam String password) {
        log.info("REST request to authenticate vendor: {}", email);
        VendorDto vendorDto = vendorService.authenticateVendor(email, password);
        return ResponseEntity.ok(vendorDto);
    }
    
    @PatchMapping("/{id}/password")
    public ResponseEntity<VendorDto> updatePassword(@PathVariable Long id,
                                                   @RequestParam String currentPassword,
                                                   @RequestParam String newPassword) {
        log.info("REST request to update password for vendor ID: {}", id);
        VendorDto vendorDto = vendorService.updatePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok(vendorDto);
    }
    
    // Statistics and analytics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getVendorStatistics() {
        log.debug("REST request to get vendor statistics");
        Map<String, Long> stats = vendorService.getVendorStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/statistics/count")
    public ResponseEntity<Long> getTotalVendorsCount() {
        log.debug("REST request to get total vendors count");
        long count = vendorService.getTotalVendorsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/active")
    public ResponseEntity<Long> getActiveVendorsCount() {
        log.debug("REST request to get active vendors count");
        long count = vendorService.getActiveVendorsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/verified")
    public ResponseEntity<Long> getVerifiedVendorsCount() {
        log.debug("REST request to get verified vendors count");
        long count = vendorService.getVerifiedVendorsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/kyc-approved")
    public ResponseEntity<Long> getKycApprovedVendorsCount() {
        log.debug("REST request to get KYC approved vendors count");
        long count = vendorService.getKycApprovedVendorsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/featured")
    public ResponseEntity<Long> getFeaturedVendorsCount() {
        log.debug("REST request to get featured vendors count");
        long count = vendorService.getFeaturedVendorsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/statistics/average-rating")
    public ResponseEntity<BigDecimal> getAverageVendorRating() {
        log.debug("REST request to get average vendor rating");
        BigDecimal rating = vendorService.getAverageVendorRating();
        return ResponseEntity.ok(rating);
    }
    
    @GetMapping("/statistics/total-revenue")
    public ResponseEntity<BigDecimal> getTotalPlatformRevenue() {
        log.debug("REST request to get total platform revenue");
        BigDecimal revenue = vendorService.getTotalPlatformRevenue();
        return ResponseEntity.ok(revenue);
    }
    
    @GetMapping("/statistics/total-orders")
    public ResponseEntity<Long> getTotalOrders() {
        log.debug("REST request to get total orders");
        Long orders = vendorService.getTotalOrders();
        return ResponseEntity.ok(orders);
    }
    
    // Recent and trending
    @GetMapping("/recent")
    public ResponseEntity<Page<VendorDto>> getRecentVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get recent vendors");
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<VendorDto> vendors = vendorService.getRecentVendors(pageRequest);
        return ResponseEntity.ok(vendors);
    }
}


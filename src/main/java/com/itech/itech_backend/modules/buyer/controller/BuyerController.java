package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.enums.KycStatus;
import com.itech.itech_backend.modules.buyer.dto.BuyerDto;
import com.itech.itech_backend.modules.buyer.dto.BuyerVerificationDto;
import com.itech.itech_backend.modules.buyer.dto.CreateBuyerDto;
import com.itech.itech_backend.modules.buyer.dto.UpdateBuyerDto;
import com.itech.itech_backend.modules.buyer.model.Buyer;
import com.itech.itech_backend.modules.buyer.service.BuyerService;
// Swagger annotations removed for compatibility
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/buyers")
@RequiredArgsConstructor
// @Tag(name = "Buyer Management", description = "APIs for managing buyers in the B2B marketplace")
public class BuyerController {

    private final BuyerService buyerService;

    // ===============================
    // CORE CRUD OPERATIONS
    // ===============================

    // @Operation removed
    @PostMapping
    public ResponseEntity<BuyerDto> createBuyer(@Valid @RequestBody CreateBuyerDto createBuyerDto) {
        log.info("Creating new buyer with email: {}", createBuyerDto.getEmail());
        BuyerDto createdBuyer = buyerService.createBuyer(createBuyerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBuyer);
    }

    // @Operation removed
    @GetMapping("/{buyerId}")
    public ResponseEntity<BuyerDto> getBuyerById(@PathVariable Long buyerId) {
        log.debug("Fetching buyer with ID: {}", buyerId);
        Optional<BuyerDto> buyer = buyerService.getBuyerById(buyerId);
        return buyer.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // @Operation removed
    @PutMapping("/{buyerId}")
    public ResponseEntity<BuyerDto> updateBuyer(@PathVariable Long buyerId, 
                                                @Valid @RequestBody UpdateBuyerDto updateBuyerDto) {
        log.info("Updating buyer with ID: {}", buyerId);
        BuyerDto updatedBuyer = buyerService.updateBuyer(buyerId, updateBuyerDto);
        return ResponseEntity.ok(updatedBuyer);
    }

    // @Operation removed
    @DeleteMapping("/{buyerId}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable Long buyerId) {
        log.info("Deleting buyer with ID: {}", buyerId);
        buyerService.deleteBuyer(buyerId);
        return ResponseEntity.noContent().build();
    }

    // @Operation removed
    @DeleteMapping("/{buyerId}/hard")
    public ResponseEntity<Void> hardDeleteBuyer(@PathVariable Long buyerId) {
        log.warn("Hard deleting buyer with ID: {}", buyerId);
        buyerService.hardDeleteBuyer(buyerId);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // AUTHENTICATION ENDPOINTS
    // ===============================

    // @Operation removed
    @PostMapping("/authenticate")
    public ResponseEntity<BuyerDto> authenticateBuyer(@RequestBody Map<String, String> credentials) {
        log.debug("Authenticating buyer with email: {}", credentials.get("email"));
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<BuyerDto> authenticatedBuyer = buyerService.authenticateBuyer(email, password);
        
        if (authenticatedBuyer.isPresent()) {
            buyerService.updateLastLogin(authenticatedBuyer.get().getId());
            return ResponseEntity.ok(authenticatedBuyer.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // @Operation removed
    @PutMapping("/{buyerId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long buyerId,
                                               @RequestBody Map<String, String> passwordData) {
        log.info("Updating password for buyer ID: {}", buyerId);
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");
        buyerService.updatePassword(buyerId, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    // @Operation removed
    @PostMapping("/password-reset/token")
    public ResponseEntity<Map<String, String>> generatePasswordResetToken(@RequestBody Map<String, String> request) {
        log.info("Generating password reset token for email: {}", request.get("email"));
        String email = request.get("email");
        String token = buyerService.generatePasswordResetToken(email);
        return ResponseEntity.ok(Map.of("resetToken", token));
    }

    // @Operation removed
    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> resetData) {
        log.info("Resetting password with token");
        String email = resetData.get("email");
        String token = resetData.get("resetToken");
        String newPassword = resetData.get("newPassword");
        buyerService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok().build();
    }

    // ===============================
    // VERIFICATION ENDPOINTS
    // ===============================

    // @Operation removed
    @PostMapping("/{buyerId}/verify/email/send")
    public ResponseEntity<Void> sendEmailVerification(@PathVariable Long buyerId) {
        log.info("Sending email verification for buyer ID: {}", buyerId);
        buyerService.sendEmailVerification(buyerId);
        return ResponseEntity.ok().build();
    }

    // @Operation removed
    @PostMapping("/{buyerId}/verify/email")
    public ResponseEntity<Map<String, Boolean>> verifyEmail(@PathVariable Long buyerId,
                                                           @RequestBody Map<String, String> verificationData) {
        log.info("Verifying email for buyer ID: {}", buyerId);
        String token = verificationData.get("token");
        boolean verified = buyerService.verifyEmail(buyerId, token);
        return ResponseEntity.ok(Map.of("verified", verified));
    }

    // @Operation removed
    @PostMapping("/{buyerId}/verify/phone/send")
    public ResponseEntity<Void> sendPhoneVerification(@PathVariable Long buyerId) {
        log.info("Sending phone verification for buyer ID: {}", buyerId);
        buyerService.sendPhoneVerification(buyerId);
        return ResponseEntity.ok().build();
    }

    // @Operation removed
    @PostMapping("/{buyerId}/verify/phone")
    public ResponseEntity<Map<String, Boolean>> verifyPhone(@PathVariable Long buyerId,
                                                           @RequestBody Map<String, String> verificationData) {
        log.info("Verifying phone for buyer ID: {}", buyerId);
        String otp = verificationData.get("otp");
        boolean verified = buyerService.verifyPhone(buyerId, otp);
        return ResponseEntity.ok(Map.of("verified", verified));
    }

    // @Operation removed
    @PostMapping("/{buyerId}/verify/kyc")
    public ResponseEntity<Void> initiateKycVerification(@PathVariable Long buyerId,
                                                        @RequestBody Map<String, Object> kycData) {
        log.info("Initiating KYC verification for buyer ID: {}", buyerId);
        buyerService.initiateKycVerification(buyerId, kycData);
        return ResponseEntity.accepted().build();
    }

    // @Operation removed")
    @PutMapping("/{buyerId}/verify/kyc/status")
    public ResponseEntity<Void> updateKycStatus(@PathVariable Long buyerId,
                                                @RequestBody Map<String, String> statusData) {
        log.info("Updating KYC status for buyer ID: {}", buyerId);
        KycStatus kycStatus = KycStatus.valueOf(statusData.get("status"));
        String reason = statusData.get("reason");
        buyerService.updateKycStatus(buyerId, kycStatus, reason);
        return ResponseEntity.ok().build();
    }

    // @Operation removed
    @GetMapping("/{buyerId}/verification")
    public ResponseEntity<BuyerVerificationDto> getBuyerVerification(@PathVariable Long buyerId) {
        log.debug("Fetching verification details for buyer ID: {}", buyerId);
        BuyerVerificationDto verification = buyerService.getBuyerVerification(buyerId);
        return ResponseEntity.ok(verification);
    }

    // @Operation removed
    @PostMapping("/{buyerId}/documents")
    public ResponseEntity<Void> uploadVerificationDocument(@PathVariable Long buyerId,
                                                          @RequestBody Map<String, String> documentData) {
        log.info("Uploading verification document for buyer ID: {}", buyerId);
        String documentType = documentData.get("documentType");
        String documentNumber = documentData.get("documentNumber");
        String documentUrl = documentData.get("documentUrl");
        buyerService.uploadVerificationDocument(buyerId, documentType, documentNumber, documentUrl);
        return ResponseEntity.ok().build();
    }

    // ===============================
    // STATUS MANAGEMENT ENDPOINTS
    // ===============================

    // @Operation removed
    @PostMapping("/{buyerId}/activate")
    public ResponseEntity<Void> activateBuyer(@PathVariable Long buyerId,
                                              @RequestBody(required = false) Map<String, String> request) {
        log.info("Activating buyer with ID: {}", buyerId);
        String reason = request != null ? request.get("reason") : "Activated by admin";
        buyerService.activateBuyer(buyerId, reason);
        return ResponseEntity.ok().build();
    }

    // @Operation removed
    @PostMapping("/{buyerId}/deactivate")
    public ResponseEntity<Void> deactivateBuyer(@PathVariable Long buyerId,
                                                @RequestBody Map<String, String> request) {
        log.info("Deactivating buyer with ID: {}", buyerId);
        String reason = request.get("reason");
        buyerService.deactivateBuyer(buyerId, reason);
        return ResponseEntity.ok().build();
    }

    // @Operation removed
    @PostMapping("/{buyerId}/suspend")
    public ResponseEntity<Void> suspendBuyer(@PathVariable Long buyerId,
                                             @RequestBody Map<String, String> request) {
        log.info("Suspending buyer with ID: {}", buyerId);
        String reason = request.get("reason");
        LocalDateTime suspensionEndDate = LocalDateTime.parse(request.get("suspensionEndDate"));
        buyerService.suspendBuyer(buyerId, reason, suspensionEndDate);
        return ResponseEntity.ok().build();
    }

    // ===============================
    // SEARCH AND FILTERING ENDPOINTS
    // ===============================

    // @Operation removed
    @GetMapping
    public ResponseEntity<Page<BuyerDto>> getAllBuyers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        log.debug("Fetching all buyers - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BuyerDto> buyers = buyerService.getAllBuyers(pageable);
        return ResponseEntity.ok(buyers);
    }

    // @Operation removed
    @GetMapping("/search")
    public ResponseEntity<Page<BuyerDto>> searchBuyers(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        log.debug("Searching buyers with term: {}", searchTerm);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BuyerDto> buyers = buyerService.searchBuyers(searchTerm, pageable);
        return ResponseEntity.ok(buyers);
    }

    // @Operation removed
    @GetMapping("/filter")
    public ResponseEntity<Page<BuyerDto>> filterBuyers(
            @RequestParam(required = false) Buyer.BuyerType buyerType,
            @RequestParam(required = false) Buyer.BuyerStatus status,
            @RequestParam(required = false) Buyer.BusinessType businessType,
            @RequestParam(required = false) Buyer.CompanySize companySize,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isPremium,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        log.debug("Filtering buyers with criteria");
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BuyerDto> buyers = buyerService.filterBuyers(buyerType, status, businessType, 
                companySize, city, state, isPremium, isVerified, pageable);
        return ResponseEntity.ok(buyers);
    }

    // @Operation removed
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<BuyerDto>> getBuyersByStatus(
            @PathVariable Buyer.BuyerStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching buyers by status: {}", status);
        Pageable pageable = PageRequest.of(page, size);
        Page<BuyerDto> buyers = buyerService.getBuyersByStatus(status, pageable);
        return ResponseEntity.ok(buyers);
    }

    // @Operation removed
    @GetMapping("/type/{buyerType}")
    public ResponseEntity<Page<BuyerDto>> getBuyersByType(
            @PathVariable Buyer.BuyerType buyerType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching buyers by type: {}", buyerType);
        Pageable pageable = PageRequest.of(page, size);
        Page<BuyerDto> buyers = buyerService.getBuyersByType(buyerType, pageable);
        return ResponseEntity.ok(buyers);
    }

    // @Operation removed
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<BuyerDto>> getBuyersByCompany(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching buyers by company ID: {}", companyId);
        Pageable pageable = PageRequest.of(page, size);
        Page<BuyerDto> buyers = buyerService.getBuyersByCompany(companyId, pageable);
        return ResponseEntity.ok(buyers);
    }

    // @Operation removed
    @GetMapping("/verified")
    public ResponseEntity<List<BuyerDto>> getVerifiedBuyers() {
        log.debug("Fetching verified buyers");
        List<BuyerDto> verifiedBuyers = buyerService.getVerifiedBuyers();
        return ResponseEntity.ok(verifiedBuyers);
    }

    // @Operation removed
    @GetMapping("/premium")
    public ResponseEntity<Page<BuyerDto>> getPremiumBuyers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching premium buyers");
        Pageable pageable = PageRequest.of(page, size);
        Page<BuyerDto> premiumBuyers = buyerService.getPremiumBuyers(pageable);
        return ResponseEntity.ok(premiumBuyers);
    }

    // ===============================
    // ANALYTICS AND REPORTING ENDPOINTS
    // ===============================

    // @Operation removed
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<Map<String, Object>> getBuyerDashboardStats() {
        log.debug("Fetching buyer dashboard statistics");
        Map<String, Object> stats = buyerService.getBuyerDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // @Operation removed
    @GetMapping("/analytics/count/status")
    public ResponseEntity<Map<Buyer.BuyerStatus, Long>> getBuyerCountByStatus() {
        log.debug("Fetching buyer count by status");
        Map<Buyer.BuyerStatus, Long> counts = buyerService.getBuyerCountByStatus();
        return ResponseEntity.ok(counts);
    }

    // @Operation removed
    @GetMapping("/analytics/count/type")
    public ResponseEntity<Map<Buyer.BuyerType, Long>> getBuyerCountByType() {
        log.debug("Fetching buyer count by type");
        Map<Buyer.BuyerType, Long> counts = buyerService.getBuyerCountByType();
        return ResponseEntity.ok(counts);
    }

    // @Operation removed
    @GetMapping("/analytics/count/verification")
    public ResponseEntity<Map<VerificationStatus, Long>> getBuyerCountByVerificationStatus() {
        log.debug("Fetching buyer count by verification status");
        Map<VerificationStatus, Long> counts = buyerService.getBuyerCountByVerificationStatus();
        return ResponseEntity.ok(counts);
    }

    // @Operation removed
    @GetMapping("/analytics/count/state")
    public ResponseEntity<Map<String, Long>> getBuyerCountByState() {
        log.debug("Fetching buyer count by state");
        Map<String, Long> counts = buyerService.getBuyerCountByState();
        return ResponseEntity.ok(counts);
    }

    // @Operation removed
    @GetMapping("/analytics/registration")
    public ResponseEntity<Map<String, Long>> getBuyerRegistrationStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.debug("Fetching buyer registration stats from {} to {}", startDate, endDate);
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Map<String, Long> stats = buyerService.getBuyerRegistrationStats(start, end);
        return ResponseEntity.ok(stats);
    }

    // @Operation removed
    @GetMapping("/analytics/premium")
    public ResponseEntity<Map<String, Object>> getPremiumBuyerStats() {
        log.debug("Fetching premium buyer statistics");
        Map<String, Object> stats = buyerService.getPremiumBuyerStats();
        return ResponseEntity.ok(stats);
    }

    // @Operation removed
    @GetMapping("/analytics/engagement")
    public ResponseEntity<Map<String, Object>> getBuyerEngagementMetrics() {
        log.debug("Fetching buyer engagement metrics");
        Map<String, Object> metrics = buyerService.getBuyerEngagementMetrics();
        return ResponseEntity.ok(metrics);
    }

    // ===============================
    // BUSINESS INTELLIGENCE ENDPOINTS
    // ===============================

    // @Operation removed
    @GetMapping("/insights/high-value")
    public ResponseEntity<List<BuyerDto>> getHighValueBuyers(
            @RequestParam BigDecimal minOrderValue) {
        log.debug("Fetching high value buyers with min order value: {}", minOrderValue);
        List<BuyerDto> highValueBuyers = buyerService.getHighValueBuyers(minOrderValue);
        return ResponseEntity.ok(highValueBuyers);
    }

    // @Operation removed
    @GetMapping("/insights/frequent")
    public ResponseEntity<Page<BuyerDto>> getFrequentBuyers(
            @RequestParam Integer minOrders,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Fetching frequent buyers with min orders: {}", minOrders);
        Pageable pageable = PageRequest.of(page, size);
        Page<BuyerDto> frequentBuyers = buyerService.getFrequentBuyers(minOrders, pageable);
        return ResponseEntity.ok(frequentBuyers);
    }

    // @Operation removed
    @GetMapping("/insights/inactive")
    public ResponseEntity<List<BuyerDto>> getInactiveBuyersForReEngagement(
            @RequestParam Integer daysSinceLastLogin) {
        log.debug("Fetching inactive buyers for re-engagement");
        List<BuyerDto> inactiveBuyers = buyerService.getInactiveBuyersForReEngagement(daysSinceLastLogin);
        return ResponseEntity.ok(inactiveBuyers);
    }

    // @Operation removed
    @GetMapping("/insights/premium-eligible")
    public ResponseEntity<List<BuyerDto>> getBuyersEligibleForPremiumOffers(
            @RequestParam BigDecimal minOrderValue) {
        log.debug("Fetching buyers eligible for premium offers");
        List<BuyerDto> eligibleBuyers = buyerService.getBuyersEligibleForPremiumOffers(minOrderValue);
        return ResponseEntity.ok(eligibleBuyers);
    }

    // @Operation removed
    @GetMapping("/insights/pending-kyc")
    public ResponseEntity<List<BuyerDto>> getBuyersWithPendingKyc(
            @RequestParam Integer daysSinceRegistration) {
        log.debug("Fetching buyers with pending KYC");
        List<BuyerDto> pendingKycBuyers = buyerService.getBuyersWithPendingKyc(daysSinceRegistration);
        return ResponseEntity.ok(pendingKycBuyers);
    }

    // ===============================
    // UTILITY ENDPOINTS
    // ===============================

    // @Operation removed
    @GetMapping("/check/email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@RequestParam String email) {
        log.debug("Checking email availability: {}", email);
        boolean available = buyerService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", available));
    }

    // @Operation removed
    @GetMapping("/check/phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneAvailability(@RequestParam String phone) {
        log.debug("Checking phone availability: {}", phone);
        boolean available = buyerService.isPhoneAvailable(phone);
        return ResponseEntity.ok(Map.of("available", available));
    }

    // @Operation removed
    @GetMapping("/{buyerId}/summary")
    public ResponseEntity<Map<String, Object>> getBuyerSummary(@PathVariable Long buyerId) {
        log.debug("Fetching buyer summary for ID: {}", buyerId);
        Map<String, Object> summary = buyerService.getBuyerSummary(buyerId);
        return ResponseEntity.ok(summary);
    }

    // @Operation removed
    @GetMapping("/{buyerId}/features/{featureName}/eligible")
    public ResponseEntity<Map<String, Boolean>> checkFeatureEligibility(
            @PathVariable Long buyerId,
            @PathVariable String featureName) {
        log.debug("Checking feature eligibility for buyer ID: {} and feature: {}", buyerId, featureName);
        boolean eligible = buyerService.checkFeatureEligibility(buyerId, featureName);
        return ResponseEntity.ok(Map.of("eligible", eligible));
    }

    // ===============================
    // BULK OPERATIONS ENDPOINTS
    // ===============================

    // @Operation removed
    @PostMapping("/bulk/status")
    public ResponseEntity<Void> bulkUpdateStatus(@RequestBody Map<String, Object> request) {
        log.info("Bulk updating buyer status");
        @SuppressWarnings("unchecked")
        List<Long> buyerIds = (List<Long>) request.get("buyerIds");
        Buyer.BuyerStatus status = Buyer.BuyerStatus.valueOf((String) request.get("status"));
        String reason = (String) request.get("reason");
        buyerService.bulkUpdateStatus(buyerIds, status, reason);
        return ResponseEntity.ok().build();
    }

    // @Operation removed
    @PostMapping("/bulk/notification")
    public ResponseEntity<Void> bulkSendNotification(@RequestBody Map<String, Object> request) {
        log.info("Bulk sending notifications");
        @SuppressWarnings("unchecked")
        List<Long> buyerIds = (List<Long>) request.get("buyerIds");
        String subject = (String) request.get("subject");
        String message = (String) request.get("message");
        String type = (String) request.get("type");
        buyerService.bulkSendNotification(buyerIds, subject, message, type);
        return ResponseEntity.accepted().build();
    }

    // @Operation removed
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportBuyers(@RequestBody Map<String, Object> request) {
        log.info("Exporting buyer data");
        @SuppressWarnings("unchecked")
        List<Long> buyerIds = (List<Long>) request.get("buyerIds");
        String format = (String) request.get("format");
        byte[] exportData = buyerService.bulkExportBuyers(buyerIds, format);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=buyers_export." + format.toLowerCase())
                .body(exportData);
    }
}


package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.modules.payment.model.Subscription;
import com.itech.itech_backend.modules.payment.repository.SubscriptionRepository;
import com.itech.itech_backend.modules.payment.service.PaymentService;
import com.itech.itech_backend.modules.vendor.dto.VendorPackagePlanDto;
import com.itech.itech_backend.modules.vendor.dto.VendorPackagePurchaseDto;
import com.itech.itech_backend.modules.vendor.model.*;
import com.itech.itech_backend.modules.vendor.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorPackageService {
    
    private final VendorPackageRepository vendorPackageRepository;
    private final VendorPackageFeatureRepository vendorPackageFeatureRepository;
    private final VendorPackageTransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final VendorsService vendorsService;
    private final PaymentService paymentService;
    
    /**
     * Get all available packages for vendor dashboard
     */
    public List<VendorPackagePlanDto> getAllPackagesForVendor() {
        List<VendorPackage> packages = vendorPackageRepository.findByIsActiveTrueOrderBySortOrderAsc();
        
        // Get current vendor's active subscription
        Vendors currentVendor = getCurrentVendor();
        Subscription currentSubscription = getCurrentVendorSubscription(currentVendor);
        
        return packages.stream()
                .map(pkg -> convertToDto(pkg, currentSubscription))
                .collect(Collectors.toList());
    }
    
    /**
     * Get package by ID
     */
    public VendorPackagePlanDto getPackageById(Long packageId) {
        VendorPackage vendorPackage = vendorPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        
        Vendors currentVendor = getCurrentVendor();
        Subscription currentSubscription = getCurrentVendorSubscription(currentVendor);
        
        return convertToDto(vendorPackage, currentSubscription);
    }
    
    /**
     * Get popular packages
     */
    public List<VendorPackagePlanDto> getPopularPackages() {
        List<VendorPackage> packages = vendorPackageRepository.findByIsPopularTrueAndIsActiveTrueOrderBySortOrderAsc();
        
        Vendors currentVendor = getCurrentVendor();
        Subscription currentSubscription = getCurrentVendorSubscription(currentVendor);
        
        return packages.stream()
                .map(pkg -> convertToDto(pkg, currentSubscription))
                .collect(Collectors.toList());
    }
    
    /**
     * Get packages by plan type
     */
    public List<VendorPackagePlanDto> getPackagesByPlanType(Subscription.PlanType planType) {
        List<VendorPackage> packages = vendorPackageRepository.findByPlanTypeAndIsActiveTrueOrderBySortOrderAsc(planType);
        
        Vendors currentVendor = getCurrentVendor();
        Subscription currentSubscription = getCurrentVendorSubscription(currentVendor);
        
        return packages.stream()
                .map(pkg -> convertToDto(pkg, currentSubscription))
                .collect(Collectors.toList());
    }
    
    /**
     * Purchase a package
     */
    @Transactional
    public VendorPackageTransaction purchasePackage(VendorPackagePurchaseDto purchaseDto) {
        log.info("Processing package purchase: {}", purchaseDto);
        
        // Get current vendor
        Vendors vendor = getCurrentVendor();
        
        // Get package
        VendorPackage vendorPackage = vendorPackageRepository.findById(purchaseDto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Package not found"));
        
        // Calculate total amount
        BigDecimal amount = vendorPackage.getDiscountedPrice() != null ? 
                vendorPackage.getDiscountedPrice() : vendorPackage.getPrice();
        BigDecimal discountAmount = purchaseDto.getDiscountAmount() != null ? 
                purchaseDto.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = calculateTax(amount.subtract(discountAmount));
        BigDecimal totalAmount = amount.subtract(discountAmount).add(taxAmount);
        
        // Generate transaction ID
        String transactionId = generateTransactionId();
        
        // Create transaction
        VendorPackageTransaction transaction = VendorPackageTransaction.builder()
                .transactionId(transactionId)
                .vendor(vendor)
                .vendorPackage(vendorPackage)
                .amount(amount)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .paymentMethod(VendorPackageTransaction.PaymentMethod.valueOf(purchaseDto.getPaymentMethod()))
                .couponCode(purchaseDto.getCouponCode())
                .billingAddress(purchaseDto.getBillingAddress())
                .billingCity(purchaseDto.getBillingCity())
                .billingState(purchaseDto.getBillingState())
                .billingPincode(purchaseDto.getBillingPincode())
                .gstNumber(purchaseDto.getGstNumber())
                .installmentCount(purchaseDto.getInstallmentCount())
                .installmentAmount(purchaseDto.getInstallmentAmount())
                .notes(purchaseDto.getNotes())
                .expiryDate(LocalDateTime.now().plusMinutes(30)) // 30 min expiry for payment
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        // Process payment based on payment method
        processPayment(transaction, purchaseDto);
        
        return transaction;
    }
    
    /**
     * Confirm payment and activate subscription
     */
    @Transactional
    public void confirmPayment(String transactionId, String paymentGatewayTransactionId, Map<String, Object> paymentDetails) {
        VendorPackageTransaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() != VendorPackageTransaction.TransactionStatus.PENDING) {
            throw new RuntimeException("Transaction is not in pending state");
        }
        
        // Update transaction
        transaction.setStatus(VendorPackageTransaction.TransactionStatus.SUCCESS);
        transaction.setPaymentGatewayTransactionId(paymentGatewayTransactionId);
        transaction.setPaymentGatewayResponse(paymentDetails.toString());
        transaction.setPaymentDate(LocalDateTime.now());
        
        if (transaction.isGenerateInvoice()) {
            transaction.setInvoiceNumber(generateInvoiceNumber());
        }
        
        transactionRepository.save(transaction);
        
        // Activate subscription
        activateSubscription(transaction);
        
        log.info("Payment confirmed for transaction: {}", transactionId);
    }
    
    /**
     * Get vendor's transaction history
     */
    public List<VendorPackageTransaction> getVendorTransactionHistory() {
        Vendors vendor = getCurrentVendor();
        return transactionRepository.findByVendorOrderByCreatedAtDesc(vendor);
    }
    
    /**
     * Get vendor's current subscription details
     */
    public Map<String, Object> getVendorSubscriptionDetails() {
        Vendors vendor = getCurrentVendor();
        Subscription currentSubscription = getCurrentVendorSubscription(vendor);
        
        Map<String, Object> details = new HashMap<>();
        if (currentSubscription != null) {
            details.put("subscription", currentSubscription);
            details.put("isActive", currentSubscription.getStatus() == Subscription.SubscriptionStatus.ACTIVE);
            details.put("daysRemaining", calculateDaysRemaining(currentSubscription));
            details.put("usageStats", getUsageStatistics(vendor, currentSubscription));
        } else {
            details.put("subscription", null);
            details.put("isActive", false);
            details.put("message", "No active subscription");
        }
        
        return details;
    }
    
    /**
     * Check if vendor can perform action based on subscription limits
     */
    public boolean canPerformAction(String action, Vendors vendor) {
        Subscription subscription = getCurrentVendorSubscription(vendor);
        if (subscription == null) {
            return false; // No subscription = no access
        }
        
        switch (action.toUpperCase()) {
            case "ADD_PRODUCT":
                return canAddProduct(vendor, subscription);
            case "ADD_LEAD":
                return canAddLead(vendor, subscription);
            case "ACCESS_ANALYTICS":
                return subscription.getAnalyticsAccess() != null && subscription.getAnalyticsAccess();
            case "PRIORITY_SUPPORT":
                return subscription.getPrioritySupport() != null && subscription.getPrioritySupport();
            case "FEATURED_LISTING":
                return subscription.getFeaturedListing() != null && subscription.getFeaturedListing();
            default:
                return false;
        }
    }
    
    /**
     * Get package comparison data
     */
    public Map<String, Object> getPackageComparison() {
        List<VendorPackage> packages = vendorPackageRepository.findByIsActiveTrueOrderBySortOrderAsc();
        
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("packages", packages.stream().map(this::convertToBasicDto).collect(Collectors.toList()));
        comparison.put("features", getAllFeatureComparison(packages));
        
        return comparison;
    }
    
    // Private helper methods
    
    private Vendors getCurrentVendor() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return vendorsService.getVendorByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
    }
    
    private Subscription getCurrentVendorSubscription(Vendors vendor) {
        return subscriptionRepository.findByVendorAndStatus(vendor, Subscription.SubscriptionStatus.ACTIVE)
                .orElse(null);
    }
    
    private VendorPackagePlanDto convertToDto(VendorPackage vendorPackage, Subscription currentSubscription) {
        VendorPackagePlanDto dto = VendorPackagePlanDto.builder()
                .id(vendorPackage.getId())
                .name(vendorPackage.getName())
                .displayName(vendorPackage.getDisplayName())
                .description(vendorPackage.getDescription())
                .price(vendorPackage.getPrice())
                .discountedPrice(vendorPackage.getDiscountedPrice())
                .durationDays(vendorPackage.getDurationDays())
                .durationType(vendorPackage.getDurationType().name())
                .planType(vendorPackage.getPlanType())
                .badge(vendorPackage.getBadge())
                .color(vendorPackage.getColor())
                .icon(vendorPackage.getIcon())
                .maxProducts(vendorPackage.getMaxProducts())
                .maxLeads(vendorPackage.getMaxLeads())
                .maxOrders(vendorPackage.getMaxOrders())
                .maxQuotations(vendorPackage.getMaxQuotations())
                .maxProductImages(vendorPackage.getMaxProductImages())
                .featuredListing(vendorPackage.getFeaturedListing())
                .prioritySupport(vendorPackage.getPrioritySupport())
                .analyticsAccess(vendorPackage.getAnalyticsAccess())
                .chatbotPriority(vendorPackage.getChatbotPriority())
                .customBranding(vendorPackage.getCustomBranding())
                .bulkImportExport(vendorPackage.getBulkImportExport())
                .apiAccess(vendorPackage.getApiAccess())
                .multiLocationSupport(vendorPackage.getMultiLocationSupport())
                .inventoryManagement(vendorPackage.getInventoryManagement())
                .customerInsights(vendorPackage.getCustomerInsights())
                .marketplaceIntegration(vendorPackage.getMarketplaceIntegration())
                .socialMediaIntegration(vendorPackage.getSocialMediaIntegration())
                .gstCompliance(vendorPackage.getGstCompliance())
                .invoiceGeneration(vendorPackage.getInvoiceGeneration())
                .paymentGateway(vendorPackage.getPaymentGateway())
                .shippingIntegration(vendorPackage.getShippingIntegration())
                .returnManagement(vendorPackage.getReturnManagement())
                .loyaltyProgram(vendorPackage.getLoyaltyProgram())
                .searchRanking(vendorPackage.getSearchRanking())
                .storageLimit(vendorPackage.getStorageLimit())
                .bandwidthLimit(vendorPackage.getBandwidthLimit())
                .apiCallLimit(vendorPackage.getApiCallLimit())
                .setupFee(vendorPackage.getSetupFee())
                .monthlyPrice(vendorPackage.getMonthlyPrice())
                .yearlyPrice(vendorPackage.getYearlyPrice())
                .trialDays(vendorPackage.getTrialDays())
                .offerText(vendorPackage.getOfferText())
                .isActive(vendorPackage.getIsActive())
                .isPopular(vendorPackage.getIsPopular())
                .sortOrder(vendorPackage.getSortOrder())
                .createdAt(vendorPackage.getCreatedAt())
                .updatedAt(vendorPackage.getUpdatedAt())
                .build();
        
        // Add features
        List<VendorPackageFeature> features = vendorPackageFeatureRepository.findByVendorPackageOrderByDisplayOrderAsc(vendorPackage);
        dto.setFeatures(features.stream().map(VendorPackageFeature::getFeatureName).collect(Collectors.toList()));
        dto.setBenefits(features.stream()
                .filter(f -> f.getFeatureType() == VendorPackageFeature.FeatureType.BENEFIT)
                .map(VendorPackageFeature::getDescription)
                .collect(Collectors.toList()));
        
        // Set current subscription info
        if (currentSubscription != null && 
            currentSubscription.getPlanType().name().equals(vendorPackage.getPlanType().name())) {
            dto.setIsCurrentPlan(true);
            dto.setSubscriptionStartDate(currentSubscription.getStartDate());
            dto.setSubscriptionEndDate(currentSubscription.getEndDate());
            dto.setSubscriptionStatus(currentSubscription.getStatus());
        } else {
            dto.setIsCurrentPlan(false);
        }
        
        return dto;
    }
    
    private Map<String, Object> convertToBasicDto(VendorPackage vendorPackage) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", vendorPackage.getId());
        dto.put("name", vendorPackage.getName());
        dto.put("displayName", vendorPackage.getDisplayName());
        dto.put("price", vendorPackage.getPrice());
        dto.put("discountedPrice", vendorPackage.getDiscountedPrice());
        dto.put("planType", vendorPackage.getPlanType());
        dto.put("maxProducts", vendorPackage.getMaxProducts());
        dto.put("maxLeads", vendorPackage.getMaxLeads());
        dto.put("featuredListing", vendorPackage.getFeaturedListing());
        dto.put("prioritySupport", vendorPackage.getPrioritySupport());
        dto.put("analyticsAccess", vendorPackage.getAnalyticsAccess());
        return dto;
    }
    
    private BigDecimal calculateTax(BigDecimal amount) {
        // 18% GST
        return amount.multiply(BigDecimal.valueOf(0.18));
    }
    
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateInvoiceNumber() {
        return "INV_" + LocalDateTime.now().getYear() + "_" + System.currentTimeMillis();
    }
    
    private void processPayment(VendorPackageTransaction transaction, VendorPackagePurchaseDto purchaseDto) {
        // For now, we'll just set status as pending
        // In real implementation, integrate with payment gateway
        log.info("Processing payment for transaction: {} with method: {}", 
                transaction.getTransactionId(), purchaseDto.getPaymentMethod());
        
        // If it's a test payment or bank transfer, mark as success immediately
        if ("TEST".equals(purchaseDto.getPaymentMethod()) || "BANK_TRANSFER".equals(purchaseDto.getPaymentMethod())) {
            transaction.setStatus(VendorPackageTransaction.TransactionStatus.SUCCESS);
            transaction.setPaymentDate(LocalDateTime.now());
            transactionRepository.save(transaction);
            activateSubscription(transaction);
        }
    }
    
    private void activateSubscription(VendorPackageTransaction transaction) {
        // Cancel existing active subscription
        subscriptionRepository.findByVendorAndStatus(transaction.getVendor(), Subscription.SubscriptionStatus.ACTIVE)
                .ifPresent(existingSubscription -> {
                    existingSubscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
                    subscriptionRepository.save(existingSubscription);
                });
        
        // Create new subscription
        VendorPackage vendorPackage = transaction.getVendorPackage();
        Subscription subscription = Subscription.builder()
                .planName(vendorPackage.getDisplayName())
                .description(vendorPackage.getDescription())
                .price(transaction.getTotalAmount().doubleValue())
                .durationDays(vendorPackage.getDurationDays())
                .planType(vendorPackage.getPlanType())
                .maxProducts(vendorPackage.getMaxProducts())
                .maxLeads(vendorPackage.getMaxLeads())
                .featuredListing(vendorPackage.getFeaturedListing())
                .prioritySupport(vendorPackage.getPrioritySupport())
                .analyticsAccess(vendorPackage.getAnalyticsAccess())
                .chatbotPriority(vendorPackage.getChatbotPriority())
                .searchRanking(vendorPackage.getSearchRanking())
                .vendor(transaction.getVendor())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(vendorPackage.getDurationDays()))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .build();
        
        subscriptionRepository.save(subscription);
        log.info("Subscription activated for vendor: {} with package: {}", 
                transaction.getVendor().getId(), vendorPackage.getName());
    }
    
    private long calculateDaysRemaining(Subscription subscription) {
        if (subscription.getEndDate() == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), subscription.getEndDate());
    }
    
    private Map<String, Object> getUsageStatistics(Vendors vendor, Subscription subscription) {
        Map<String, Object> stats = new HashMap<>();
        // TODO: Implement actual usage tracking
        stats.put("productsUsed", 0);
        stats.put("leadsUsed", 0);
        stats.put("storageUsed", 0);
        stats.put("apiCallsUsed", 0);
        return stats;
    }
    
    private boolean canAddProduct(Vendors vendor, Subscription subscription) {
        if (subscription.getMaxProducts() == null) {
            return true; // Unlimited
        }
        // TODO: Get actual product count for vendor
        int currentProducts = 0;
        return currentProducts < subscription.getMaxProducts();
    }
    
    private boolean canAddLead(Vendors vendor, Subscription subscription) {
        if (subscription.getMaxLeads() == null) {
            return true; // Unlimited
        }
        // TODO: Get actual lead count for vendor
        int currentLeads = 0;
        return currentLeads < subscription.getMaxLeads();
    }
    
    private Map<String, List<Map<String, Object>>> getAllFeatureComparison(List<VendorPackage> packages) {
        Map<String, List<Map<String, Object>>> comparison = new HashMap<>();
        
        // Get all unique features
        Set<String> allFeatures = new HashSet<>();
        for (VendorPackage pkg : packages) {
            List<VendorPackageFeature> features = vendorPackageFeatureRepository.findByVendorPackageOrderByDisplayOrderAsc(pkg);
            allFeatures.addAll(features.stream().map(VendorPackageFeature::getFeatureName).collect(Collectors.toSet()));
        }
        
        // Add core features
        allFeatures.addAll(Arrays.asList("Max Products", "Max Leads", "Featured Listing", "Priority Support", "Analytics Access"));
        
        List<Map<String, Object>> featureList = new ArrayList<>();
        for (String feature : allFeatures) {
            Map<String, Object> featureComparison = new HashMap<>();
            featureComparison.put("feature", feature);
            
            Map<String, Object> packageSupport = new HashMap<>();
            for (VendorPackage pkg : packages) {
                packageSupport.put(pkg.getName(), hasFeature(pkg, feature));
            }
            featureComparison.put("packages", packageSupport);
            featureList.add(featureComparison);
        }
        
        comparison.put("features", featureList);
        return comparison;
    }
    
    private boolean hasFeature(VendorPackage vendorPackage, String featureName) {
        // Check boolean features
        switch (featureName) {
            case "Featured Listing":
                return vendorPackage.getFeaturedListing() != null && vendorPackage.getFeaturedListing();
            case "Priority Support":
                return vendorPackage.getPrioritySupport() != null && vendorPackage.getPrioritySupport();
            case "Analytics Access":
                return vendorPackage.getAnalyticsAccess() != null && vendorPackage.getAnalyticsAccess();
            // Add more feature checks as needed
        }
        
        // Check custom features
        List<VendorPackageFeature> features = vendorPackageFeatureRepository.findByVendorPackageOrderByDisplayOrderAsc(vendorPackage);
        return features.stream().anyMatch(f -> f.getFeatureName().equals(featureName) && f.getIsIncluded());
    }
}

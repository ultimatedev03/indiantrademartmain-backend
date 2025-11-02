package com.itech.itech_backend.modules.vendor.controller;

import com.itech.itech_backend.modules.payment.model.Subscription;
import com.itech.itech_backend.modules.vendor.dto.VendorPackagePlanDto;
import com.itech.itech_backend.modules.vendor.dto.VendorPackagePurchaseDto;
import com.itech.itech_backend.modules.vendor.model.VendorPackageTransaction;
import com.itech.itech_backend.modules.vendor.service.VendorPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor/packages")
@RequiredArgsConstructor
@Slf4j
public class VendorPackageController {
    
    private final VendorPackageService vendorPackageService;
    
    /**
     * Get all available packages for vendor
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getAllPackages() {
        try {
            List<VendorPackagePlanDto> packages = vendorPackageService.getAllPackagesForVendor();
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Packages retrieved successfully",
                    "data", packages,
                    "count", packages.size()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving packages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve packages: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get popular packages
     */
    @GetMapping("/popular")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getPopularPackages() {
        try {
            List<VendorPackagePlanDto> packages = vendorPackageService.getPopularPackages();
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Popular packages retrieved successfully",
                    "data", packages,
                    "count", packages.size()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving popular packages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve popular packages: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get packages by plan type (SILVER, GOLD, PLATINUM, DIAMOND)
     */
    @GetMapping("/type/{planType}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getPackagesByType(@PathVariable String planType) {
        try {
            Subscription.PlanType type = Subscription.PlanType.valueOf(planType.toUpperCase());
            List<VendorPackagePlanDto> packages = vendorPackageService.getPackagesByPlanType(type);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Packages for " + planType + " retrieved successfully",
                    "data", packages,
                    "planType", planType,
                    "count", packages.size()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Invalid plan type: " + planType
                    ));
        } catch (Exception e) {
            log.error("Error retrieving packages by type: " + planType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve packages: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get package details by ID
     */
    @GetMapping("/{packageId}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getPackageById(@PathVariable Long packageId) {
        try {
            VendorPackagePlanDto packageDto = vendorPackageService.getPackageById(packageId);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Package retrieved successfully",
                    "data", packageDto
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving package: " + packageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve package: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Purchase a package
     */
    @PostMapping("/purchase")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> purchasePackage(@Valid @RequestBody VendorPackagePurchaseDto purchaseDto) {
        try {
            log.info("Processing package purchase request: {}", purchaseDto);
            
            VendorPackageTransaction transaction = vendorPackageService.purchasePackage(purchaseDto);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Package purchase initiated successfully",
                    "transactionId", transaction.getTransactionId(),
                    "totalAmount", transaction.getTotalAmount(),
                    "status", transaction.getStatus().toString(),
                    "expiryDate", transaction.getExpiryDate()
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error processing package purchase", e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Unexpected error during package purchase", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to process purchase: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Confirm payment (webhook endpoint for payment gateway)
     */
    @PostMapping("/confirm-payment")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            String transactionId = (String) paymentData.get("transactionId");
            String paymentGatewayTransactionId = (String) paymentData.get("paymentGatewayTransactionId");
            
            if (transactionId == null || paymentGatewayTransactionId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Missing required payment data"
                        ));
            }
            
            vendorPackageService.confirmPayment(transactionId, paymentGatewayTransactionId, paymentData);
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Payment confirmed and subscription activated"
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error confirming payment", e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Unexpected error during payment confirmation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to confirm payment: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get vendor's current subscription details
     */
    @GetMapping("/subscription/current")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getCurrentSubscription() {
        try {
            Map<String, Object> subscriptionDetails = vendorPackageService.getVendorSubscriptionDetails();
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Subscription details retrieved successfully",
                    "data", subscriptionDetails
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving subscription details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve subscription: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get vendor's transaction history
     */
    @GetMapping("/transactions/history")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getTransactionHistory() {
        try {
            List<VendorPackageTransaction> transactions = vendorPackageService.getVendorTransactionHistory();
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Transaction history retrieved successfully",
                    "data", transactions,
                    "count", transactions.size()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving transaction history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve transaction history: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get package comparison data
     */
    @GetMapping("/comparison")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getPackageComparison() {
        try {
            Map<String, Object> comparison = vendorPackageService.getPackageComparison();
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Package comparison retrieved successfully",
                    "data", comparison
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving package comparison", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve package comparison: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Check if vendor can perform specific actions
     */
    @GetMapping("/permissions/check/{action}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> checkPermission(@PathVariable String action) {
        try {
            // This would typically get the vendor from the security context
            // For now, we'll return a simple response
            Map<String, Object> response = Map.of(
                    "success", true,
                    "action", action,
                    "allowed", true, // This would come from vendorPackageService.canPerformAction()
                    "message", "Permission check completed"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking permissions for action: " + action, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to check permissions: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get vendor dashboard summary with package info
     */
    @GetMapping("/dashboard/summary")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            Map<String, Object> subscriptionDetails = vendorPackageService.getVendorSubscriptionDetails();
            List<VendorPackagePlanDto> popularPackages = vendorPackageService.getPopularPackages();
            
            Map<String, Object> summary = Map.of(
                    "currentSubscription", subscriptionDetails,
                    "popularPackages", popularPackages.stream().limit(3).toList(),
                    "upgradeRecommendations", getUpgradeRecommendations(subscriptionDetails)
            );
            
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Dashboard summary retrieved successfully",
                    "data", summary
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving dashboard summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to retrieve dashboard summary: " + e.getMessage()
                    ));
        }
    }
    
    private List<String> getUpgradeRecommendations(Map<String, Object> subscriptionDetails) {
        // Simple upgrade recommendations based on current subscription
        Boolean isActive = (Boolean) subscriptionDetails.get("isActive");
        if (isActive == null || !isActive) {
            return List.of(
                    "Subscribe to Silver plan to get started",
                    "Gold plan recommended for growing businesses",
                    "Platinum plan offers advanced analytics"
            );
        }
        
        return List.of(
                "Consider upgrading for more features",
                "Premium plans offer better visibility",
                "Advanced analytics available in higher tiers"
        );
    }
}

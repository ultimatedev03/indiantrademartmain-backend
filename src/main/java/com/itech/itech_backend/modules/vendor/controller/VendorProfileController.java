package com.itech.itech_backend.modules.vendor.controller;

import com.itech.itech_backend.modules.vendor.model.VendorProfile;
import com.itech.itech_backend.modules.vendor.service.VendorProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor Profile Management", description = "APIs for vendor profile operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VendorProfileController {

    private final VendorProfileService vendorProfileService;

    @Operation(summary = "Create vendor profile", description = "Register a new vendor profile")
    @PostMapping("/register")
    public ResponseEntity<?> createVendorProfile(@Valid @RequestBody VendorProfile vendorProfile) {
        try {
            VendorProfile createdVendor = vendorProfileService.createVendorProfile(vendorProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Vendor profile created successfully",
                "data", createdVendor
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to create vendor profile: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Get vendor profile", description = "Get vendor profile by ID")
    @GetMapping("/{vendorId}")
    public ResponseEntity<?> getVendorProfile(
            @Parameter(description = "Vendor ID") @PathVariable Long vendorId) {
        try {
            VendorProfile vendor = vendorProfileService.getVendorProfileById(vendorId);
            
            // Increment profile views
            vendorProfileService.incrementProfileViews(vendorId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", vendor
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update vendor profile", description = "Update vendor profile information")
    @PutMapping("/{vendorId}")
    public ResponseEntity<?> updateVendorProfile(
            @Parameter(description = "Vendor ID") @PathVariable Long vendorId,
            @Valid @RequestBody VendorProfile vendorProfile) {
        try {
            VendorProfile updatedVendor = vendorProfileService.updateVendorProfile(vendorId, vendorProfile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vendor profile updated successfully",
                "data", updatedVendor
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to update vendor profile: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Search vendors", description = "Search vendors with keyword and filters")
    @GetMapping("/search")
    public ResponseEntity<?> searchVendors(
            @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by state") @RequestParam(required = false) String state,
            @Parameter(description = "Minimum rating") @RequestParam(required = false) BigDecimal minRating,
            @Parameter(description = "Filter verified vendors only") @RequestParam(defaultValue = "false") boolean verified,
            @PageableDefault(size = 20, sort = {"averageRating"}, direction = Sort.Direction.DESC) Pageable pageable) {
        
        try {
            Page<VendorProfile> vendors;
            
            if (city != null || state != null) {
                vendors = vendorProfileService.getVendorsByLocation(city, state, pageable);
            } else if (minRating != null) {
                vendors = vendorProfileService.getVendorsByMinRating(minRating, pageable);
            } else {
                vendors = vendorProfileService.searchVendors(keyword, pageable);
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", vendors.getContent(),
                "pagination", Map.of(
                    "currentPage", vendors.getNumber(),
                    "totalPages", vendors.getTotalPages(),
                    "totalElements", vendors.getTotalElements(),
                    "pageSize", vendors.getSize()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Search failed: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Get featured vendors", description = "Get list of featured vendors")
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedVendors() {
        try {
            List<VendorProfile> featuredVendors = vendorProfileService.getFeaturedVendors();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", featuredVendors
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to fetch featured vendors: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Get top rated vendors", description = "Get vendors sorted by rating")
    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedVendors(
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<VendorProfile> vendors = vendorProfileService.getTopRatedVendors(pageable);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", vendors.getContent(),
                "pagination", Map.of(
                    "currentPage", vendors.getNumber(),
                    "totalPages", vendors.getTotalPages(),
                    "totalElements", vendors.getTotalElements()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to fetch top rated vendors: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Get vendor by company ID", description = "Find vendor profile by company ID")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getVendorByCompanyId(
            @Parameter(description = "Company ID") @PathVariable Long companyId) {
        try {
            Optional<VendorProfile> vendor = vendorProfileService.findByCompanyId(companyId);
            if (vendor.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", vendor.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to find vendor: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Get vendor by user ID", description = "Find vendor profile by user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getVendorByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        try {
            Optional<VendorProfile> vendor = vendorProfileService.findByUserId(userId);
            if (vendor.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", vendor.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to find vendor: " + e.getMessage()
            ));
        }
    }

    // Admin endpoints
    @Operation(summary = "Approve vendor", description = "Approve vendor KYC (Admin only)")
    @PutMapping("/{vendorId}/approve")
    public ResponseEntity<?> approveVendor(
            @Parameter(description = "Vendor ID") @PathVariable Long vendorId,
            @RequestParam String approvedBy) {
        try {
            VendorProfile approvedVendor = vendorProfileService.approveVendor(vendorId, approvedBy);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vendor approved successfully",
                "data", approvedVendor
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to approve vendor: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Reject vendor", description = "Reject vendor KYC (Admin only)")
    @PutMapping("/{vendorId}/reject")
    public ResponseEntity<?> rejectVendor(
            @Parameter(description = "Vendor ID") @PathVariable Long vendorId,
            @RequestParam String reason,
            @RequestParam String rejectedBy) {
        try {
            VendorProfile rejectedVendor = vendorProfileService.rejectVendor(vendorId, reason, rejectedBy);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vendor rejected",
                "data", rejectedVendor
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to reject vendor: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Get pending verifications", description = "Get vendors pending KYC verification (Admin only)")
    @GetMapping("/admin/pending-verifications")
    public ResponseEntity<?> getPendingVerifications() {
        try {
            List<VendorProfile> pendingVendors = vendorProfileService.getPendingVerifications();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", pendingVendors
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to fetch pending verifications: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Get vendor analytics", description = "Get vendor statistics (Admin only)")
    @GetMapping("/admin/analytics")
    public ResponseEntity<?> getVendorAnalytics() {
        try {
            Map<String, Object> analytics = Map.of(
                "totalVendors", vendorProfileService.getTotalVendorCount(),
                "verifiedVendors", vendorProfileService.getVerifiedVendorCount(),
                "pendingApproval", vendorProfileService.getVendorCountByStatus(VendorProfile.VendorStatus.PENDING),
                "approvedVendors", vendorProfileService.getVendorCountByStatus(VendorProfile.VendorStatus.APPROVED),
                "averageRating", vendorProfileService.getAverageVendorRating().orElse(BigDecimal.ZERO)
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analytics
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to fetch analytics: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Set featured status", description = "Set or unset vendor as featured (Admin only)")
    @PutMapping("/{vendorId}/featured")
    public ResponseEntity<?> setFeaturedStatus(
            @Parameter(description = "Vendor ID") @PathVariable Long vendorId,
            @RequestParam boolean featured) {
        try {
            VendorProfile updatedVendor = vendorProfileService.setFeaturedStatus(vendorId, featured);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Featured status updated successfully",
                "data", updatedVendor
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to update featured status: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "Upgrade vendor type", description = "Upgrade vendor subscription type")
    @PutMapping("/{vendorId}/upgrade")
    public ResponseEntity<?> upgradeVendorType(
            @Parameter(description = "Vendor ID") @PathVariable Long vendorId,
            @RequestParam VendorProfile.VendorType vendorType) {
        try {
            VendorProfile upgradedVendor = vendorProfileService.upgradeVendorType(vendorId, vendorType);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vendor type upgraded successfully",
                "data", upgradedVendor
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to upgrade vendor type: " + e.getMessage()
            ));
        }
    }
}

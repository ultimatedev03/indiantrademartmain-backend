package com.itech.itech_backend.modules.buyer.controller;

import com.itech.itech_backend.modules.buyer.model.Inquiry;
import com.itech.itech_backend.modules.buyer.service.InquiryService;
import com.itech.itech_backend.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@Slf4j
public class InquiryController {

    private final InquiryService inquiryService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createInquiry(@RequestBody Map<String, Object> inquiryData, 
                                          HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            Long productId = Long.valueOf(inquiryData.get("productId").toString());
            String message = inquiryData.get("message").toString();
            
            Inquiry createdInquiry = inquiryService.createInquiry(userId, productId, message);
            return ResponseEntity.ok(createdInquiry);
            
        } catch (Exception e) {
            log.error("Error creating inquiry", e);
            return ResponseEntity.badRequest().body("Failed to create inquiry: " + e.getMessage());
        }
    }

    @PostMapping("/direct")
    public ResponseEntity<Inquiry> createInquiryDirect(@RequestBody Inquiry inquiry) {
        Inquiry createdInquiry = inquiryService.createInquiry(inquiry);
        return ResponseEntity.ok(createdInquiry);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Inquiry>> getAllInquiries() {
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        return ResponseEntity.ok(inquiries);
    }
    
    @GetMapping("/my-inquiries")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyInquiries(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        try {
            Long userId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Inquiry> inquiries = inquiryService.getInquiriesByUser(userId, pageable);
            return ResponseEntity.ok(inquiries);
            
        } catch (Exception e) {
            log.error("Error fetching user inquiries", e);
            return ResponseEntity.badRequest().body("Failed to fetch inquiries");
        }
    }
    
    @GetMapping("/vendor-inquiries")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getVendorInquiries(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest request) {
        try {
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                return ResponseEntity.badRequest().body("Vendor not authenticated");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Inquiry> inquiries = inquiryService.getInquiriesByVendor(vendorId, pageable);
            return ResponseEntity.ok(inquiries);
            
        } catch (Exception e) {
            log.error("Error fetching vendor inquiries", e);
            return ResponseEntity.badRequest().body("Failed to fetch inquiries");
        }
    }
    
    @GetMapping("/vendor-unresolved")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getUnresolvedInquiries(HttpServletRequest request) {
        try {
            Long vendorId = jwtTokenUtil.extractUserIdFromRequest(request);
            if (vendorId == null) {
                return ResponseEntity.badRequest().body("Vendor not authenticated");
            }
            
            List<Inquiry> inquiries = inquiryService.getUnresolvedInquiriesByVendor(vendorId);
            return ResponseEntity.ok(inquiries);
            
        } catch (Exception e) {
            log.error("Error fetching unresolved inquiries", e);
            return ResponseEntity.badRequest().body("Failed to fetch inquiries");
        }
    }
    
    @PutMapping("/{inquiryId}/resolve")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<?> markAsResolved(@PathVariable Long inquiryId) {
        try {
            Inquiry inquiry = inquiryService.markAsResolved(inquiryId);
            return ResponseEntity.ok(inquiry);
        } catch (Exception e) {
            log.error("Error marking inquiry as resolved", e);
            return ResponseEntity.badRequest().body("Failed to resolve inquiry");
        }
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getInquiriesByProduct(@PathVariable Long productId) {
        try {
            List<Inquiry> inquiries = inquiryService.getAllInquiries();
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            log.error("Error fetching product inquiries", e);
            return ResponseEntity.badRequest().body("Failed to fetch inquiries");
        }
    }
}


package com.itech.itech_backend.modules.buyer.service;

import com.itech.itech_backend.modules.buyer.model.Inquiry;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.buyer.repository.InquiryRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.shared.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final BuyerProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public Inquiry createInquiry(Long userId, Long productId, String message) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            Inquiry inquiry = Inquiry.builder()
                .user(user)
                .product(product)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
            
            Inquiry savedInquiry = inquiryRepository.save(inquiry);
            
            // Send notification to vendor
            notifyVendor(savedInquiry);
            
            return savedInquiry;
            
        } catch (Exception e) {
            log.error("Error creating inquiry", e);
            throw new RuntimeException("Failed to create inquiry: " + e.getMessage());
        }
    }

    public Inquiry createInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }
    
    public Page<Inquiry> getInquiriesByUser(Long userId, Pageable pageable) {
        return inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    public Page<Inquiry> getInquiriesByVendor(Long vendorId, Pageable pageable) {
        return inquiryRepository.findByProductVendorIdOrderByCreatedAtDesc(vendorId, pageable);
    }
    
    public List<Inquiry> getUnresolvedInquiriesByVendor(Long vendorId) {
        return inquiryRepository.findByProductVendorIdAndIsResolvedFalseOrderByCreatedAtDesc(vendorId);
    }
    
    public Inquiry markAsResolved(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new RuntimeException("Inquiry not found"));
        
        inquiry.setResolved(true);
        inquiry.setUpdatedAt(LocalDateTime.now());
        
        return inquiryRepository.save(inquiry);
    }
    
    public Long getUnresolvedInquiryCount(Long vendorId) {
        return inquiryRepository.countByProductVendorIdAndIsResolvedFalse(vendorId);
    }
    
    private void notifyVendor(Inquiry inquiry) {
        try {
            Vendors vendor = inquiry.getProduct().getVendor();
            String subject = "New Inquiry for Your Product: " + inquiry.getProduct().getName();
            String body = String.format(
                "Dear %s,\n\n" +
                "You have received a new inquiry for your product '%s'.\n\n" +
                "Inquiry from: %s\n" +
                "Email: %s\n" +
                "Message: %s\n\n" +
                "Please login to your dashboard to respond to this inquiry.\n\n" +
                "Best regards,\n" +
                "Indian Trade Mart Team",
                vendor.getName(),
                inquiry.getProduct().getName(),
                inquiry.getUser().getName(),
                inquiry.getUser().getEmail(),
                inquiry.getMessage()
            );
            
            emailService.sendEmail(vendor.getEmail(), subject, body);
            
        } catch (Exception e) {
            log.error("Failed to notify vendor about new inquiry", e);
            // Don't throw exception as inquiry is already saved
        }
    }
}


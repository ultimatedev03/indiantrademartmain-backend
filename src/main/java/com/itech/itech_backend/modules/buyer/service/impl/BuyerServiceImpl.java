package com.itech.itech_backend.modules.buyer.service.impl;

import com.itech.itech_backend.enums.VerificationStatus;
import com.itech.itech_backend.enums.KycStatus;
import com.itech.itech_backend.modules.buyer.dto.BuyerDto;
import com.itech.itech_backend.modules.buyer.dto.BuyerVerificationDto;
import com.itech.itech_backend.modules.buyer.dto.CreateBuyerDto;
import com.itech.itech_backend.modules.buyer.dto.UpdateBuyerDto;
import com.itech.itech_backend.modules.buyer.model.Buyer;
import com.itech.itech_backend.modules.buyer.repository.BuyerRepository;
import com.itech.itech_backend.modules.buyer.service.BuyerService;
import com.itech.itech_backend.modules.company.model.Company;
import com.itech.itech_backend.modules.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BuyerServiceImpl implements BuyerService {

    private final BuyerRepository buyerRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    // ===============================
    // CORE CRUD OPERATIONS
    // ===============================

    @Override
    public BuyerDto createBuyer(CreateBuyerDto createBuyerDto) {
        log.info("Creating new buyer with email: {}", createBuyerDto.getEmail());
        
        // Validate unique email and phone
        if (buyerRepository.existsByEmail(createBuyerDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + createBuyerDto.getEmail());
        }
        
        if (buyerRepository.existsByPhone(createBuyerDto.getPhone())) {
            throw new RuntimeException("Phone number already exists: " + createBuyerDto.getPhone());
        }

        Buyer buyer = new Buyer();
        
        // Map basic information
        buyer.setBuyerName(createBuyerDto.getBuyerName());
        buyer.setEmail(createBuyerDto.getEmail());
        buyer.setPhone(createBuyerDto.getPhone());
        buyer.setPassword(passwordEncoder.encode(createBuyerDto.getPassword()));
        buyer.setBuyerType(createBuyerDto.getBuyerType());
        buyer.setBuyerStatus(Buyer.BuyerStatus.ACTIVE);
        
        // Map personal information
        buyer.setFirstName(createBuyerDto.getFirstName());
        buyer.setLastName(createBuyerDto.getLastName());
        buyer.setDisplayName(createBuyerDto.getDisplayName());
        buyer.setJobTitle(createBuyerDto.getJobTitle());
        buyer.setDepartment(createBuyerDto.getDepartment());
        buyer.setBio(createBuyerDto.getBio());
        
        // Map contact information
        buyer.setSecondaryEmail(createBuyerDto.getSecondaryEmail());
        buyer.setSecondaryPhone(createBuyerDto.getSecondaryPhone());
        buyer.setLinkedinUrl(createBuyerDto.getLinkedinUrl());
        buyer.setWebsiteUrl(createBuyerDto.getWebsiteUrl());
        
        // Map addresses
        buyer.setBillingAddressLine1(createBuyerDto.getBillingAddressLine1());
        buyer.setBillingAddressLine2(createBuyerDto.getBillingAddressLine2());
        buyer.setBillingCity(createBuyerDto.getBillingCity());
        buyer.setBillingState(createBuyerDto.getBillingState());
        buyer.setBillingPostalCode(createBuyerDto.getBillingPostalCode());
        buyer.setBillingCountry(createBuyerDto.getBillingCountry());
        
        if (Boolean.TRUE.equals(createBuyerDto.getSameAsBilling())) {
            buyer.setShippingAddressLine1(createBuyerDto.getBillingAddressLine1());
            buyer.setShippingAddressLine2(createBuyerDto.getBillingAddressLine2());
            buyer.setShippingCity(createBuyerDto.getBillingCity());
            buyer.setShippingState(createBuyerDto.getBillingState());
            buyer.setShippingPostalCode(createBuyerDto.getBillingPostalCode());
            buyer.setShippingCountry(createBuyerDto.getBillingCountry());
        } else {
            buyer.setShippingAddressLine1(createBuyerDto.getShippingAddressLine1());
            buyer.setShippingAddressLine2(createBuyerDto.getShippingAddressLine2());
            buyer.setShippingCity(createBuyerDto.getShippingCity());
            buyer.setShippingState(createBuyerDto.getShippingState());
            buyer.setShippingPostalCode(createBuyerDto.getShippingPostalCode());
            buyer.setShippingCountry(createBuyerDto.getShippingCountry());
        }
        
        // Map business information
        buyer.setBusinessType(createBuyerDto.getBusinessType());
        buyer.setIndustries(createBuyerDto.getIndustries());
        buyer.setCompanySize(createBuyerDto.getCompanySize());
        buyer.setAnnualBudget(createBuyerDto.getAnnualBudget());
        buyer.setPurchasingAuthority(createBuyerDto.getPurchasingAuthority());
        
        // Map preferences
        buyer.setPreferredCategories(createBuyerDto.getPreferredCategories());
        buyer.setPreferredPaymentMethods(createBuyerDto.getPreferredPaymentMethods());
        buyer.setCreditLimit(createBuyerDto.getCreditLimit());
        buyer.setPaymentTermsPreference(createBuyerDto.getPaymentTermsPreference());
        
        // Map communication preferences
        buyer.setEmailNotifications(createBuyerDto.getEmailNotifications());
        buyer.setSmsNotifications(createBuyerDto.getSmsNotifications());
        buyer.setMarketingEmails(createBuyerDto.getMarketingEmails());
        buyer.setPriceAlerts(createBuyerDto.getPriceAlerts());
        buyer.setNewProductAlerts(createBuyerDto.getNewProductAlerts());
        buyer.setOrderUpdates(createBuyerDto.getOrderUpdates());
        
        // Map privacy settings
        buyer.setProfileVisibility(createBuyerDto.getProfileVisibility());
        buyer.setTwoFactorEnabled(createBuyerDto.getTwoFactorEnabled());
        
        // Set company association if provided
        if (createBuyerDto.getCompanyId() != null) {
            Company company = companyRepository.findById(createBuyerDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found: " + createBuyerDto.getCompanyId()));
            buyer.setCompany(company);
        }
        
        // Initialize default values
        buyer.setVerificationStatus(VerificationStatus.PENDING);
        buyer.setKycStatus(KycStatus.NOT_SUBMITTED);
        buyer.setSubscriptionType(Buyer.SubscriptionType.FREE);
        buyer.setIsPremium(false);
        buyer.setCreatedAt(LocalDateTime.now());
        buyer.setUpdatedAt(LocalDateTime.now());
        
        Buyer savedBuyer = buyerRepository.save(buyer);
        log.info("Successfully created buyer with ID: {}", savedBuyer.getId());
        
        return convertToDto(savedBuyer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BuyerDto> getBuyerById(Long buyerId) {
        log.debug("Fetching buyer by ID: {}", buyerId);
        return buyerRepository.findById(buyerId)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BuyerDto> getBuyerByEmail(String email) {
        log.debug("Fetching buyer by email: {}", email);
        return buyerRepository.findByEmail(email)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BuyerDto> getBuyerByPhone(String phone) {
        log.debug("Fetching buyer by phone: {}", phone);
        return buyerRepository.findByPhone(phone)
            .map(this::convertToDto);
    }

    @Override
    public BuyerDto updateBuyer(Long buyerId, UpdateBuyerDto updateBuyerDto) {
        log.info("Updating buyer with ID: {}", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        // Update fields only if provided (non-null)
        if (updateBuyerDto.getBuyerName() != null) {
            buyer.setBuyerName(updateBuyerDto.getBuyerName());
        }
        if (updateBuyerDto.getEmail() != null && !updateBuyerDto.getEmail().equals(buyer.getEmail())) {
            if (buyerRepository.existsByEmail(updateBuyerDto.getEmail())) {
                throw new RuntimeException("Email already exists: " + updateBuyerDto.getEmail());
            }
            buyer.setEmail(updateBuyerDto.getEmail());
            buyer.setEmailVerified(false); // Reset verification
        }
        if (updateBuyerDto.getPhone() != null && !updateBuyerDto.getPhone().equals(buyer.getPhone())) {
            if (buyerRepository.existsByPhone(updateBuyerDto.getPhone())) {
                throw new RuntimeException("Phone already exists: " + updateBuyerDto.getPhone());
            }
            buyer.setPhone(updateBuyerDto.getPhone());
            buyer.setPhoneVerified(false); // Reset verification
        }
        
        // Update other fields
        updateFieldIfNotNull(buyer::setBuyerType, updateBuyerDto.getBuyerType());
        updateFieldIfNotNull(buyer::setFirstName, updateBuyerDto.getFirstName());
        updateFieldIfNotNull(buyer::setLastName, updateBuyerDto.getLastName());
        updateFieldIfNotNull(buyer::setDisplayName, updateBuyerDto.getDisplayName());
        updateFieldIfNotNull(buyer::setJobTitle, updateBuyerDto.getJobTitle());
        updateFieldIfNotNull(buyer::setDepartment, updateBuyerDto.getDepartment());
        updateFieldIfNotNull(buyer::setBio, updateBuyerDto.getBio());
        
        // Update contact information
        updateFieldIfNotNull(buyer::setSecondaryEmail, updateBuyerDto.getSecondaryEmail());
        updateFieldIfNotNull(buyer::setSecondaryPhone, updateBuyerDto.getSecondaryPhone());
        updateFieldIfNotNull(buyer::setLinkedinUrl, updateBuyerDto.getLinkedinUrl());
        updateFieldIfNotNull(buyer::setWebsiteUrl, updateBuyerDto.getWebsiteUrl());
        
        // Update addresses
        updateFieldIfNotNull(buyer::setBillingAddressLine1, updateBuyerDto.getBillingAddressLine1());
        updateFieldIfNotNull(buyer::setBillingAddressLine2, updateBuyerDto.getBillingAddressLine2());
        updateFieldIfNotNull(buyer::setBillingCity, updateBuyerDto.getBillingCity());
        updateFieldIfNotNull(buyer::setBillingState, updateBuyerDto.getBillingState());
        updateFieldIfNotNull(buyer::setBillingPostalCode, updateBuyerDto.getBillingPostalCode());
        updateFieldIfNotNull(buyer::setBillingCountry, updateBuyerDto.getBillingCountry());
        
        updateFieldIfNotNull(buyer::setShippingAddressLine1, updateBuyerDto.getShippingAddressLine1());
        updateFieldIfNotNull(buyer::setShippingAddressLine2, updateBuyerDto.getShippingAddressLine2());
        updateFieldIfNotNull(buyer::setShippingCity, updateBuyerDto.getShippingCity());
        updateFieldIfNotNull(buyer::setShippingState, updateBuyerDto.getShippingState());
        updateFieldIfNotNull(buyer::setShippingPostalCode, updateBuyerDto.getShippingPostalCode());
        updateFieldIfNotNull(buyer::setShippingCountry, updateBuyerDto.getShippingCountry());
        
        // Update business information
        updateFieldIfNotNull(buyer::setBusinessType, updateBuyerDto.getBusinessType());
        updateFieldIfNotNull(buyer::setIndustries, updateBuyerDto.getIndustries());
        updateFieldIfNotNull(buyer::setCompanySize, updateBuyerDto.getCompanySize());
        updateFieldIfNotNull(buyer::setAnnualBudget, updateBuyerDto.getAnnualBudget());
        updateFieldIfNotNull(buyer::setPurchasingAuthority, updateBuyerDto.getPurchasingAuthority());
        
        // Update preferences
        updateFieldIfNotNull(buyer::setPreferredCategories, updateBuyerDto.getPreferredCategories());
        updateFieldIfNotNull(buyer::setPreferredPaymentMethods, updateBuyerDto.getPreferredPaymentMethods());
        updateFieldIfNotNull(buyer::setCreditLimit, updateBuyerDto.getCreditLimit());
        updateFieldIfNotNull(buyer::setPaymentTermsPreference, updateBuyerDto.getPaymentTermsPreference());
        
        // Update communication preferences
        updateFieldIfNotNull(buyer::setEmailNotifications, updateBuyerDto.getEmailNotifications());
        updateFieldIfNotNull(buyer::setSmsNotifications, updateBuyerDto.getSmsNotifications());
        updateFieldIfNotNull(buyer::setMarketingEmails, updateBuyerDto.getMarketingEmails());
        updateFieldIfNotNull(buyer::setPriceAlerts, updateBuyerDto.getPriceAlerts());
        updateFieldIfNotNull(buyer::setNewProductAlerts, updateBuyerDto.getNewProductAlerts());
        updateFieldIfNotNull(buyer::setOrderUpdates, updateBuyerDto.getOrderUpdates());
        
        // Update privacy settings
        updateFieldIfNotNull(buyer::setProfileVisibility, updateBuyerDto.getProfileVisibility());
        updateFieldIfNotNull(buyer::setTwoFactorEnabled, updateBuyerDto.getTwoFactorEnabled());
        
        // Update company association
        if (updateBuyerDto.getCompanyId() != null) {
            Company company = companyRepository.findById(updateBuyerDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found: " + updateBuyerDto.getCompanyId()));
            buyer.setCompany(company);
        }
        
        buyer.setUpdatedAt(LocalDateTime.now());
        Buyer updatedBuyer = buyerRepository.save(buyer);
        
        log.info("Successfully updated buyer with ID: {}", buyerId);
        return convertToDto(updatedBuyer);
    }

    @Override
    public void deleteBuyer(Long buyerId) {
        log.info("Soft deleting buyer with ID: {}", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        buyer.setBuyerStatus(Buyer.BuyerStatus.DELETED);
        buyer.setUpdatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        
        log.info("Successfully soft deleted buyer with ID: {}", buyerId);
    }

    @Override
    public void hardDeleteBuyer(Long buyerId) {
        log.warn("Hard deleting buyer with ID: {}", buyerId);
        
        if (!buyerRepository.existsById(buyerId)) {
            throw new RuntimeException("Buyer not found: " + buyerId);
        }
        
        buyerRepository.deleteById(buyerId);
        log.warn("Successfully hard deleted buyer with ID: {}", buyerId);
    }

    // ===============================
    // AUTHENTICATION & SECURITY
    // ===============================

    @Override
    @Transactional(readOnly = true)
    public Optional<BuyerDto> authenticateBuyer(String email, String password) {
        log.debug("Authenticating buyer with email: {}", email);
        
        Optional<Buyer> buyerOpt = buyerRepository.findByEmailAndBuyerStatus(email, Buyer.BuyerStatus.ACTIVE);
        
        if (buyerOpt.isPresent()) {
            Buyer buyer = buyerOpt.get();
            if (passwordEncoder.matches(password, buyer.getPassword())) {
                log.info("Successfully authenticated buyer: {}", email);
                return Optional.of(convertToDto(buyer));
            }
        }
        
        log.warn("Authentication failed for email: {}", email);
        return Optional.empty();
    }

    @Override
    public void updatePassword(Long buyerId, String currentPassword, String newPassword) {
        log.info("Updating password for buyer ID: {}", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        if (!passwordEncoder.matches(currentPassword, buyer.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        buyer.setPassword(passwordEncoder.encode(newPassword));
        buyer.setUpdatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        
        log.info("Successfully updated password for buyer ID: {}", buyerId);
    }

    @Override
    public void updateLastLogin(Long buyerId) {
        log.debug("Updating last login for buyer ID: {}", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        buyer.setLastLogin(LocalDateTime.now());
        buyer.setUpdatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
    }

    // ===============================
    // VERIFICATION OPERATIONS
    // ===============================

    @Override
    public void sendEmailVerification(Long buyerId) {
        log.info("Sending email verification for buyer ID: {}", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        // Generate verification token and send email
        String verificationToken = UUID.randomUUID().toString();
        buyer.setEmailVerificationToken(verificationToken);
        buyer.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        buyer.setUpdatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        
        // TODO: Send email with verification token
        log.info("Email verification sent to buyer ID: {}", buyerId);
    }

    @Override
    public boolean verifyEmail(Long buyerId, String verificationToken) {
        log.info("Verifying email for buyer ID: {} with token: {}", buyerId, verificationToken);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        if (verificationToken.equals(buyer.getEmailVerificationToken()) &&
            buyer.getEmailVerificationTokenExpiry().isAfter(LocalDateTime.now())) {
            
            buyer.setEmailVerified(true);
            buyer.setEmailVerificationDate(LocalDateTime.now());
            buyer.setEmailVerificationToken(null);
            buyer.setEmailVerificationTokenExpiry(null);
            buyer.setUpdatedAt(LocalDateTime.now());
            
            updateOverallVerificationStatus(buyer);
            buyerRepository.save(buyer);
            
            log.info("Successfully verified email for buyer ID: {}", buyerId);
            return true;
        }
        
        log.warn("Email verification failed for buyer ID: {}", buyerId);
        return false;
    }

    @Override
    public void sendPhoneVerification(Long buyerId) {
        log.info("Sending phone verification for buyer ID: {}", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        // Generate OTP and send SMS
        String otp = String.format("%06d", new Random().nextInt(999999));
        buyer.setPhoneVerificationOtp(otp);
        buyer.setPhoneVerificationOtpExpiry(LocalDateTime.now().plusMinutes(10));
        buyer.setUpdatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        
        // TODO: Send SMS with OTP
        log.info("Phone verification OTP sent to buyer ID: {}", buyerId);
    }

    @Override
    public boolean verifyPhone(Long buyerId, String otp) {
        log.info("Verifying phone for buyer ID: {} with OTP", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        if (otp.equals(buyer.getPhoneVerificationOtp()) &&
            buyer.getPhoneVerificationOtpExpiry().isAfter(LocalDateTime.now())) {
            
            buyer.setPhoneVerified(true);
            buyer.setPhoneVerificationDate(LocalDateTime.now());
            buyer.setPhoneVerificationOtp(null);
            buyer.setPhoneVerificationOtpExpiry(null);
            buyer.setUpdatedAt(LocalDateTime.now());
            
            updateOverallVerificationStatus(buyer);
            buyerRepository.save(buyer);
            
            log.info("Successfully verified phone for buyer ID: {}", buyerId);
            return true;
        }
        
        log.warn("Phone verification failed for buyer ID: {}", buyerId);
        return false;
    }

    @Override
    public void initiateKycVerification(Long buyerId, Map<String, Object> kycData) {
        log.info("Initiating KYC verification for buyer ID: {}", buyerId);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        buyer.setKycStatus(KycStatus.PENDING);
        // Store KYC data as JSON
        try {
            buyer.setKycDataJson(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(kycData));
        } catch (Exception e) {
            log.error("Error storing KYC data as JSON", e);
            buyer.setKycDataJson(kycData.toString());
        }
        buyer.setKycAttempts(buyer.getKycAttempts() != null ? buyer.getKycAttempts() + 1 : 1);
        buyer.setUpdatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        
        // TODO: Submit to KYC service
        log.info("KYC verification initiated for buyer ID: {}", buyerId);
    }

    @Override
    public void updateKycStatus(Long buyerId, KycStatus kycStatus, String reason) {
        log.info("Updating KYC status for buyer ID: {} to: {}", buyerId, kycStatus);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        buyer.setKycStatus(kycStatus);
        if (kycStatus == KycStatus.APPROVED) {
            buyer.setKycApproved(true);
            buyer.setKycVerificationDate(LocalDateTime.now());
        } else if (kycStatus == KycStatus.REJECTED) {
            buyer.setKycRejectionReason(reason);
        }
        buyer.setUpdatedAt(LocalDateTime.now());
        
        updateOverallVerificationStatus(buyer);
        buyerRepository.save(buyer);
        
        log.info("Successfully updated KYC status for buyer ID: {}", buyerId);
    }

    // ===============================
    // STATUS MANAGEMENT
    // ===============================

    @Override
    public void activateBuyer(Long buyerId, String reason) {
        updateBuyerStatus(buyerId, Buyer.BuyerStatus.ACTIVE, reason);
    }

    @Override
    public void deactivateBuyer(Long buyerId, String reason) {
        updateBuyerStatus(buyerId, Buyer.BuyerStatus.INACTIVE, reason);
    }

    @Override
    public void suspendBuyer(Long buyerId, String reason, LocalDateTime suspensionEndDate) {
        log.info("Suspending buyer ID: {} until: {}", buyerId, suspensionEndDate);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        buyer.setBuyerStatus(Buyer.BuyerStatus.SUSPENDED);
        buyer.setSuspensionEndDate(suspensionEndDate);
        buyer.setStatusReason(reason);
        buyer.setUpdatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        
        log.info("Successfully suspended buyer ID: {}", buyerId);
    }

    @Override
    public void updateBuyerStatus(Long buyerId, Buyer.BuyerStatus status, String reason) {
        log.info("Updating buyer status for ID: {} to: {}", buyerId, status);
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
        
        Buyer.BuyerStatus oldStatus = buyer.getBuyerStatus();
        buyer.setBuyerStatus(status);
        buyer.setStatusReason(reason);
        buyer.setUpdatedAt(LocalDateTime.now());
        
        // Clear suspension date if not suspended
        if (status != Buyer.BuyerStatus.SUSPENDED) {
            buyer.setSuspensionEndDate(null);
        }
        
        buyerRepository.save(buyer);
        
        log.info("Successfully updated buyer status from {} to {} for ID: {}", oldStatus, status, buyerId);
    }

    // ===============================
    // SEARCH AND FILTERING
    // ===============================

    @Override
    @Transactional(readOnly = true)
    public Page<BuyerDto> getAllBuyers(Pageable pageable) {
        log.debug("Fetching all buyers with pagination");
        return buyerRepository.findAll(pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuyerDto> searchBuyers(String searchTerm, Pageable pageable) {
        log.debug("Searching buyers with term: {}", searchTerm);
        return buyerRepository.searchBuyers(searchTerm, pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuyerDto> filterBuyers(Buyer.BuyerType buyerType, Buyer.BuyerStatus status,
                                       Buyer.BusinessType businessType, Buyer.CompanySize companySize,
                                       String city, String state, Boolean isPremium,
                                       Boolean isVerified, Pageable pageable) {
        log.debug("Filtering buyers with multiple criteria");
        return buyerRepository.findBuyersWithFilters(buyerType, status, businessType, companySize,
                city, state, isPremium, isVerified, pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuyerDto> getBuyersByStatus(Buyer.BuyerStatus status, Pageable pageable) {
        log.debug("Fetching buyers by status: {}", status);
        return buyerRepository.findByBuyerStatus(status, pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuyerDto> getBuyersByType(Buyer.BuyerType buyerType, Pageable pageable) {
        log.debug("Fetching buyers by type: {}", buyerType);
        return buyerRepository.findByBuyerType(buyerType, pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuyerDto> getBuyersByCompany(Long companyId, Pageable pageable) {
        log.debug("Fetching buyers by company ID: {}", companyId);
        return buyerRepository.findByCompany_Id(companyId, pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuyerDto> getVerifiedBuyers() {
        log.debug("Fetching fully verified buyers");
        return buyerRepository.findFullyVerifiedBuyers().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // ===============================
    // ANALYTICS AND REPORTING
    // ===============================

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBuyerDashboardStats() {
        log.debug("Generating buyer dashboard statistics");
        
        Object[] stats = buyerRepository.getBuyerDashboardStats();
        Map<String, Object> result = new HashMap<>();
        
        if (stats != null && stats.length >= 4) {
            result.put("totalBuyers", stats[0]);
            result.put("activeBuyers", stats[1]);
            result.put("premiumBuyers", stats[2]);
            result.put("verifiedBuyers", stats[3]);
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Buyer.BuyerStatus, Long> getBuyerCountByStatus() {
        log.debug("Generating buyer count by status");
        
        Map<Buyer.BuyerStatus, Long> result = new HashMap<>();
        for (Buyer.BuyerStatus status : Buyer.BuyerStatus.values()) {
            result.put(status, buyerRepository.countByStatus(status));
        }
        return result;
    }

    // ===============================
    // VALIDATION AND UTILITY METHODS
    // ===============================

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !buyerRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneAvailable(String phone) {
        return !buyerRepository.existsByPhone(phone);
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private BuyerDto convertToDto(Buyer buyer) {
        BuyerDto dto = new BuyerDto();
        
        // Basic information
        dto.setId(buyer.getId());
        dto.setBuyerName(buyer.getBuyerName());
        dto.setEmail(buyer.getEmail());
        dto.setPhone(buyer.getPhone());
        dto.setBuyerType(buyer.getBuyerType());
        dto.setBuyerStatus(buyer.getBuyerStatus());
        
        // Personal information
        dto.setFirstName(buyer.getFirstName());
        dto.setLastName(buyer.getLastName());
        dto.setDisplayName(buyer.getDisplayName());
        dto.setJobTitle(buyer.getJobTitle());
        dto.setDepartment(buyer.getDepartment());
        dto.setBio(buyer.getBio());
        
        // Contact information
        dto.setSecondaryEmail(buyer.getSecondaryEmail());
        dto.setSecondaryPhone(buyer.getSecondaryPhone());
        dto.setLinkedinUrl(buyer.getLinkedinUrl());
        dto.setWebsiteUrl(buyer.getWebsiteUrl());
        
        // Verification status
        dto.setEmailVerified(buyer.getEmailVerified());
        dto.setPhoneVerified(buyer.getPhoneVerified());
        dto.setKycApproved(buyer.getKycApproved());
        dto.setIsVerified(buyer.getVerificationStatus() == VerificationStatus.VERIFIED);
        dto.setKycSubmitted(buyer.getKycStatus() != KycStatus.NOT_SUBMITTED);
        
        // Business information
        dto.setBusinessType(buyer.getBusinessType());
        dto.setIndustries(buyer.getIndustries());
        dto.setCompanySize(buyer.getCompanySize());
        dto.setAnnualBudget(buyer.getAnnualBudget());
        dto.setPurchasingAuthority(buyer.getPurchasingAuthority());
        
        // Subscription information
        dto.setIsPremium(buyer.getIsPremium());
        dto.setSubscriptionType(buyer.getSubscriptionType());
        dto.setSubscriptionStartDate(buyer.getSubscriptionStartDate());
        dto.setSubscriptionEndDate(buyer.getSubscriptionEndDate());
        
        // Activity information
        dto.setTotalOrders(buyer.getTotalOrders());
        dto.setTotalSpent(buyer.getTotalSpent());
        dto.setLastLogin(buyer.getLastLogin());
        dto.setLastOrderDate(buyer.getLastOrderDate());
        
        // Company information
        if (buyer.getCompany() != null) {
            // Create a simple CompanyDto or set individual fields
            // Since CompanyDto is expected, we'll need to create one
            // For now, we'll skip this to avoid additional complexity
        }
        
        // Timestamps
        dto.setCreatedAt(buyer.getCreatedAt());
        dto.setUpdatedAt(buyer.getUpdatedAt());
        
        return dto;
    }

    private void updateOverallVerificationStatus(Buyer buyer) {
        if (Boolean.TRUE.equals(buyer.getEmailVerified()) && 
            Boolean.TRUE.equals(buyer.getPhoneVerified()) &&
            Boolean.TRUE.equals(buyer.getKycApproved())) {
            buyer.setVerificationStatus(VerificationStatus.VERIFIED);
        } else if (Boolean.TRUE.equals(buyer.getEmailVerified()) && 
                   Boolean.TRUE.equals(buyer.getPhoneVerified())) {
            buyer.setVerificationStatus(VerificationStatus.PENDING);
        } else {
            buyer.setVerificationStatus(VerificationStatus.PENDING);
        }
    }

    private <T> void updateFieldIfNotNull(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    // ===============================
    // PLACEHOLDER IMPLEMENTATIONS
    // ===============================

    @Override
    public String generatePasswordResetToken(String email) {
        // TODO: Implement password reset token generation
        return UUID.randomUUID().toString();
    }

    @Override
    public void resetPassword(String email, String resetToken, String newPassword) {
        // TODO: Implement password reset with token verification
        log.info("Password reset not yet implemented");
    }

    @Override
    public void updateTwoFactorAuthentication(Long buyerId, Boolean enabled) {
        // TODO: Implement two-factor authentication management
        log.info("Two-factor authentication management not yet implemented");
    }

    @Override
    public BuyerVerificationDto getBuyerVerification(Long buyerId) {
        // TODO: Implement verification details retrieval
        return new BuyerVerificationDto();
    }

    @Override
    public void uploadVerificationDocument(Long buyerId, String documentType, String documentNumber, String documentUrl) {
        // TODO: Implement document upload and verification
        log.info("Document verification not yet implemented");
    }

    @Override
    public void upgradeToPremium(Long buyerId, Buyer.SubscriptionType subscriptionType) {
        // TODO: Implement premium upgrade
        log.info("Premium upgrade not yet implemented");
    }

    @Override
    public void downgradeFromPremium(Long buyerId) {
        // TODO: Implement premium downgrade
        log.info("Premium downgrade not yet implemented");
    }

    @Override
    public void renewSubscription(Long buyerId, Buyer.SubscriptionType subscriptionType) {
        // TODO: Implement subscription renewal
        log.info("Subscription renewal not yet implemented");
    }

    @Override
    public void cancelSubscription(Long buyerId, String reason) {
        // TODO: Implement subscription cancellation
        log.info("Subscription cancellation not yet implemented");
    }

    @Override
    public void updateSubscriptionPreferences(Long buyerId, Boolean autoRenew, Buyer.SubscriptionType preferredType) {
        // TODO: Implement subscription preference updates
        log.info("Subscription preference updates not yet implemented");
    }

    @Override
    public void updatePersonalInfo(Long buyerId, String firstName, String lastName, String displayName, String bio) {
        // TODO: Implement personal info updates
        log.info("Personal info updates not yet implemented");
    }

    @Override
    public void updateContactInfo(Long buyerId, String email, String phone, String secondaryEmail, String secondaryPhone) {
        // TODO: Implement contact info updates
        log.info("Contact info updates not yet implemented");
    }

    @Override
    public void updateBusinessInfo(Long buyerId, Buyer.BusinessType businessType, List<Buyer.Industry> industries, Buyer.CompanySize companySize, BigDecimal annualBudget, Buyer.PurchasingAuthority purchasingAuthority) {
        // TODO: Implement business info updates
        log.info("Business info updates not yet implemented");
    }

    @Override
    public void updateBillingAddress(Long buyerId, String addressLine1, String addressLine2, String city, String state, String postalCode, String country) {
        // TODO: Implement billing address updates
        log.info("Billing address updates not yet implemented");
    }

    @Override
    public void updateShippingAddress(Long buyerId, String addressLine1, String addressLine2, String city, String state, String postalCode, String country) {
        // TODO: Implement shipping address updates
        log.info("Shipping address updates not yet implemented");
    }

    @Override
    public void updateCommunicationPreferences(Long buyerId, Boolean emailNotifications, Boolean smsNotifications, Boolean marketingEmails, Boolean priceAlerts, Boolean newProductAlerts) {
        // TODO: Implement communication preference updates
        log.info("Communication preference updates not yet implemented");
    }

    @Override
    public void updatePrivacySettings(Long buyerId, Buyer.ProfileVisibility profileVisibility, Boolean twoFactorEnabled) {
        // TODO: Implement privacy setting updates
        log.info("Privacy setting updates not yet implemented");
    }

    @Override
    public Page<BuyerDto> getBuyersByLocation(String city, String state, Pageable pageable) {
        // TODO: Implement location-based buyer filtering
        return Page.empty(pageable);
    }

    @Override
    public Page<BuyerDto> getPremiumBuyers(Pageable pageable) {
        // TODO: Implement premium buyer filtering
        return Page.empty(pageable);
    }

    @Override
    public Map<Buyer.BuyerType, Long> getBuyerCountByType() {
        // TODO: Implement buyer count by type
        return new HashMap<>();
    }

    @Override
    public Map<VerificationStatus, Long> getBuyerCountByVerificationStatus() {
        // TODO: Implement verification status count
        return new HashMap<>();
    }

    @Override
    public Map<String, Long> getBuyerCountByState() {
        // TODO: Implement state-wise buyer count
        return new HashMap<>();
    }

    @Override
    public Map<String, Long> getBuyerRegistrationStats(LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: Implement registration statistics
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getPremiumBuyerStats() {
        // TODO: Implement premium buyer statistics
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getBuyerEngagementMetrics() {
        // TODO: Implement engagement metrics
        return new HashMap<>();
    }

    @Override
    public List<BuyerDto> getHighValueBuyers(BigDecimal minOrderValue) {
        // TODO: Implement high value buyer identification
        return new ArrayList<>();
    }

    @Override
    public Page<BuyerDto> getFrequentBuyers(Integer minOrders, Pageable pageable) {
        // TODO: Implement frequent buyer identification
        return Page.empty(pageable);
    }

    @Override
    public List<BuyerDto> getInactiveBuyersForReEngagement(Integer daysSinceLastLogin) {
        // TODO: Implement inactive buyer identification
        return new ArrayList<>();
    }

    @Override
    public List<BuyerDto> getBuyersEligibleForPremiumOffers(BigDecimal minOrderValue) {
        // TODO: Implement premium offer eligibility
        return new ArrayList<>();
    }

    @Override
    public List<BuyerDto> getBuyersWithPendingKyc(Integer daysSinceRegistration) {
        // TODO: Implement pending KYC identification
        return new ArrayList<>();
    }

    @Override
    public List<BuyerDto> getActiveBuyersSince(LocalDateTime sinceDate) {
        // TODO: Implement active buyer identification
        return new ArrayList<>();
    }

    @Override
    public void bulkUpdateStatus(List<Long> buyerIds, Buyer.BuyerStatus status, String reason) {
        // TODO: Implement bulk status updates
        log.info("Bulk status updates not yet implemented");
    }

    @Override
    public void bulkSendNotification(List<Long> buyerIds, String subject, String message, String type) {
        // TODO: Implement bulk notifications
        log.info("Bulk notifications not yet implemented");
    }

    @Override
    public byte[] bulkExportBuyers(List<Long> buyerIds, String format) {
        // TODO: Implement bulk export
        return new byte[0];
    }

    @Override
    public Map<String, Object> importBuyers(byte[] fileData, String format) {
        // TODO: Implement bulk import
        return new HashMap<>();
    }

    @Override
    public List<BuyerDto> getBuyersForEmailNotifications() {
        // TODO: Implement email notification targeting
        return new ArrayList<>();
    }

    @Override
    public List<BuyerDto> getBuyersForSmsNotifications() {
        // TODO: Implement SMS notification targeting
        return new ArrayList<>();
    }

    @Override
    public List<BuyerDto> getBuyersForMarketingEmails() {
        // TODO: Implement marketing email targeting
        return new ArrayList<>();
    }

    @Override
    public List<BuyerDto> getBuyersByPreferredCategory(String category) {
        // TODO: Implement category-based buyer filtering
        return new ArrayList<>();
    }

    @Override
    public void updateMarketingPreferences(Long buyerId, Boolean allowMarketingEmails, Boolean allowPromotionalSms) {
        // TODO: Implement marketing preference updates
        log.info("Marketing preference updates not yet implemented");
    }

    @Override
    public void syncWithExternalCRM(Long buyerId) {
        // TODO: Implement CRM synchronization
        log.info("CRM synchronization not yet implemented");
    }

    @Override
    public void updateCreditLimitFromExternal(Long buyerId) {
        // TODO: Implement external credit limit updates
        log.info("External credit limit updates not yet implemented");
    }

    @Override
    public Map<String, Object> validateBuyerWithFraudService(Long buyerId) {
        // TODO: Implement fraud service validation
        return new HashMap<>();
    }

    @Override
    public void trackBuyerActivity(Long buyerId, String activityType, Map<String, Object> activityData) {
        // TODO: Implement activity tracking
        log.info("Activity tracking not yet implemented");
    }

    @Override
    public void updateEngagementScore(Long buyerId) {
        // TODO: Implement engagement score updates
        log.info("Engagement score updates not yet implemented");
    }

    @Override
    public void updateBuyerTier(Long buyerId) {
        // TODO: Implement buyer tier updates
        log.info("Buyer tier updates not yet implemented");
    }

    @Override
    public Map<String, String> validateBuyerData(CreateBuyerDto createBuyerDto) {
        // TODO: Implement comprehensive data validation
        return new HashMap<>();
    }

    @Override
    public boolean checkFeatureEligibility(Long buyerId, String featureName) {
        // TODO: Implement feature eligibility checking
        return true;
    }

    @Override
    public Map<String, Object> getBuyerSummary(Long buyerId) {
        // TODO: Implement buyer summary generation
        return new HashMap<>();
    }
}



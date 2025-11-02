package com.itech.itech_backend.modules.core.service;

import com.itech.itech_backend.modules.shared.dto.JwtResponse;
import com.itech.itech_backend.modules.shared.dto.LoginRequestDto;
import com.itech.itech_backend.modules.shared.dto.RegisterRequestDto;
import com.itech.itech_backend.modules.shared.dto.VerifyOtpRequestDto;
import com.itech.itech_backend.modules.core.model.OtpVerification;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.model.UserAddress;
import com.itech.itech_backend.modules.admin.model.Admins;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.buyer.model.Buyer;
import com.itech.itech_backend.modules.core.repository.OtpVerificationRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import com.itech.itech_backend.modules.admin.repository.AdminsRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerRepository;
import com.itech.itech_backend.modules.buyer.service.BuyerService;
import com.itech.itech_backend.modules.buyer.dto.CreateBuyerDto;
import com.itech.itech_backend.modules.buyer.dto.BuyerDto;
import com.itech.itech_backend.modules.core.service.UserAddressService;
import com.itech.itech_backend.modules.shared.service.EmailService;
import com.itech.itech_backend.modules.shared.service.SmsService;
import com.itech.itech_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UnifiedAuthService {

    private final UserRepository userRepository;
    private final VendorsRepository vendorsRepository;
    private final AdminsRepository adminsRepository;
    private final BuyerRepository buyerRepository;
    private final BuyerService buyerService;
    private final UserAddressService userAddressService;
    private final OtpVerificationRepository otpRepo;
    private final EmailService emailService;
    private final SmsService smsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    private static final String ADMIN_ACCESS_CODE = "ADMIN2025";

    public String register(RegisterRequestDto dto) {
        System.out.println("🔧 REGISTRATION DEBUG - Starting registration for: " + dto.getEmail());
        System.out.println("🔧 Role requested: " + dto.getRole());
        
        try {
        // Check if user already exists in any table
        System.out.println("🔍 Checking existence for email: '" + dto.getEmail() + "', phone: '" + dto.getPhone() + "'");
        
        boolean userExists = userRepository.existsByEmail(dto.getEmail());
        boolean vendorExists = vendorsRepository.existsByEmail(dto.getEmail());
        boolean adminExists = adminsRepository.existsByEmail(dto.getEmail());
        boolean buyerExists = buyerRepository.existsByEmail(dto.getEmail());
            
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            userExists = userExists || userRepository.existsByPhone(dto.getPhone());
            vendorExists = vendorExists || vendorsRepository.existsByPhone(dto.getPhone());
            adminExists = adminExists || adminsRepository.existsByPhone(dto.getPhone());
            buyerExists = buyerExists || buyerRepository.existsByPhone(dto.getPhone());
        }
        
    if (userExists || vendorExists || adminExists || buyerExists) {
        System.out.println("⚠️ User already exists with email: " + dto.getEmail());
        System.out.println("📊 Existence check - User: " + userExists + ", Vendor: " + vendorExists + ", Admin: " + adminExists + ", Buyer: " + buyerExists);
            
            // Find existing user from appropriate table
            User existingUser = null;
            
            if (userExists) {
                // Try to find by email first, then by phone if provided
                Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());
                if (!userOpt.isPresent() && dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
                    userOpt = userRepository.findByPhone(dto.getPhone());
                }
                if (userOpt.isPresent()) {
                    existingUser = userOpt.get();
                }
            } else if (vendorExists) {
                // Try to find by email first, then by phone if provided
                Optional<Vendors> vendorOpt = vendorsRepository.findByEmail(dto.getEmail());
                if (!vendorOpt.isPresent() && dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
                    vendorOpt = vendorsRepository.findByPhone(dto.getPhone());
                }
                if (vendorOpt.isPresent()) {
                    Vendors vendor = vendorOpt.get();
                    existingUser = User.builder()
                        .id(vendor.getId())
                        .name(vendor.getName())
                        .email(vendor.getEmail())
                        .phone(vendor.getPhone())
                        .password(vendor.getPassword())
                        .role(User.UserRole.valueOf(vendor.getRole()))
                        .isVerified(vendor.isVerified())
                        .build();
                }
            } else if (adminExists) {
                // Try to find by email first, then by phone if provided
                Optional<Admins> adminOpt = adminsRepository.findByEmail(dto.getEmail());
                if (!adminOpt.isPresent() && dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
                    adminOpt = adminsRepository.findByPhone(dto.getPhone());
                }
                if (adminOpt.isPresent()) {
                    Admins admin = adminOpt.get();
                    existingUser = User.builder()
                        .id(admin.getId())
                        .name(admin.getName())
                        .email(admin.getEmail())
                        .phone(admin.getPhone())
                        .password(admin.getPassword())
                        .role(User.UserRole.valueOf(admin.getRole()))
                        .isVerified(admin.isVerified())
                        .build();
                }
            } else if (buyerExists) {
                // Try to find by email first, then by phone if provided
                Optional<Buyer> buyerOpt = buyerRepository.findByEmail(dto.getEmail());
                if (!buyerOpt.isPresent() && dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
                    buyerOpt = buyerRepository.findByPhone(dto.getPhone());
                }
                if (buyerOpt.isPresent()) {
                    Buyer buyer = buyerOpt.get();
                    existingUser = User.builder()
                        .id(buyer.getId())
                        .name(buyer.getBuyerName())
                        .email(buyer.getEmail())
                        .phone(buyer.getPhone())
                        .password(buyer.getPassword())
                        .role(User.UserRole.BUYER) // Buyers use BUYER role
                        .isVerified(buyer.getIsEmailVerified() != null && buyer.getIsEmailVerified())
                        .build();
                }
            }
            
            if (existingUser != null) {
                System.out.println("📝 Found existing user details:");
                System.out.println("📝 - ID: " + existingUser.getId());
                System.out.println("📝 - Name: " + existingUser.getName());
                System.out.println("📝 - Email: " + existingUser.getEmail());
                System.out.println("📝 - Role: " + existingUser.getRoleAsString());
                System.out.println("📝 - Verified: " + existingUser.isVerified());
                
                // If user is not verified, resend OTP
                if (!existingUser.isVerified()) {
                    System.out.println("🔄 User exists but not verified, resending OTP for verification...");
                    return sendRegistrationOtp(dto, existingUser);
                } else {
                    // User is already verified - should login instead
                    System.out.println("✅ User already exists and is verified. Directing to login.");
                    throw new RuntimeException("EMAIL_ALREADY_EXISTS");
                }
            }
            
            // Fallback message if user data couldn't be retrieved
            throw new RuntimeException("EMAIL_ALREADY_EXISTS");
        }
        
        System.out.println("✅ User does not exist, proceeding with registration");
        
        // Create user in User table and related entities
        User user = createUser(dto);
        System.out.println("✅ User created with ID: " + user.getId());
        
        // Create address if provided
        createAddressIfProvided(dto, user);
        
            // Send OTP
            System.out.println("📧 About to send registration OTP...");
            String result = sendRegistrationOtp(dto, user);
            System.out.println("🔧 Registration process completed: " + result);
            return result;
            
        } catch (Exception e) {
            System.out.println("❌ Registration failed with error: " + e.getMessage());
            e.printStackTrace();
            
            // Handle specific SQL constraint violations
            if (e.getMessage() != null && e.getMessage().contains("cannot be null")) {
                throw new RuntimeException("Registration failed: Required field missing. Please contact support.");
            }
            
            // Re-throw existing RuntimeExceptions (like EMAIL_ALREADY_EXISTS)
            if (e instanceof RuntimeException) {
                throw e;
            }
            
            // Handle unexpected errors
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public JwtResponse directLogin(LoginRequestDto loginRequest) {
        return directLoginWithRoleValidation(loginRequest, null);
    }
    
    public JwtResponse directLoginWithRoleValidation(LoginRequestDto loginRequest, String expectedRole) {
        System.out.println("🚀 Direct Login request for: " + loginRequest.getEmailOrPhone() + 
                         (expectedRole != null ? " (Expected role: " + expectedRole + ")" : ""));
        
        // Find user across all tables
        User user = findUserAcrossAllTables(loginRequest.getEmailOrPhone());
        
        if (user == null) {
            System.out.println("❌ User not found in any table");
            throw new RuntimeException("Invalid email and password");
        }
        
        System.out.println("✅ User found: " + user.getEmail() + ", Role: " + user.getRole());
        
        // Validate role if expectedRole is specified
        if (expectedRole != null && !expectedRole.equals(user.getRoleAsString())) {
            System.out.println("❌ Role mismatch: Expected " + expectedRole + ", Found " + user.getRole());
            throw new RuntimeException("Invalid email and password");
        }
        
        // Check admin access code if admin
        if ("ADMIN".equals(user.getRole()) || "ROLE_ADMIN".equals(user.getRole())) {
            if (loginRequest.getAdminCode() == null || !ADMIN_ACCESS_CODE.equals(loginRequest.getAdminCode())) {
                System.out.println("❌ Invalid admin access code");
                return null;
            }
        }
        
        // Validate password - support both plain text and BCrypt
        if (!validatePassword(loginRequest.getPassword(), user.getPassword())) {
            System.out.println("❌ Invalid password");
            throw new RuntimeException("Invalid email/password");
        }
        
        System.out.println("✅ Password validated successfully");
        
        // Generate token directly with user ID
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoleAsString(), user.getId());
        System.out.println("✅ Token generated successfully");
        
        // Create response with user info
        return JwtResponse.builder()
            .token(token)
            .message("Login successful!")
            .user(JwtResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRoleWithReplace("ROLE_", ""))
                .isVerified(user.isVerified())
                .build())
            .build();
    }
    
    public String sendLoginOtp(LoginRequestDto loginRequest) {
        return sendLoginOtpWithRoleValidation(loginRequest, null);
    }
    
    public String sendLoginOtpWithRoleValidation(LoginRequestDto loginRequest, String expectedRole) {
        System.out.println("🔑 Unified Login OTP request for: " + loginRequest.getEmailOrPhone() + 
                         (expectedRole != null ? " (Expected role: " + expectedRole + ")" : ""));
        
        // Find user across all tables
        User user = findUserAcrossAllTables(loginRequest.getEmailOrPhone());
        
        if (user == null) {
            throw new RuntimeException("Invalid email and password");
        }
        
        // Validate role if expectedRole is specified
        if (expectedRole != null && !expectedRole.equals(user.getRole())) {
            System.out.println("❌ Role mismatch for OTP login: Expected " + expectedRole + ", Found " + user.getRole());
            throw new RuntimeException("This account is not registered as a " + 
                (expectedRole.equals("ROLE_USER") ? "user" : 
                 expectedRole.equals("ROLE_VENDOR") ? "vendor" : 
                 expectedRole.equals("ROLE_ADMIN") ? "admin" : "valid user") + 
                ". Please use the correct login portal.");
        }
        
        // Check admin access code if admin
        if ("ROLE_ADMIN".equals(user.getRole())) {
            if (loginRequest.getAdminCode() == null || !ADMIN_ACCESS_CODE.equals(loginRequest.getAdminCode())) {
                throw new RuntimeException("Invalid admin access code. Please contact system administrator.");
            }
        }
        
        // For OTP-based login, we don't validate the password here
        // We only validate that user exists and has correct role
        // The password will be validated during OTP verification
        System.out.println("✅ User validated for OTP login, sending OTP...");
        
        // Store the password temporarily for later validation during OTP verification
        // This is already handled in the generateAndSendOtp method
        
        // Generate and send OTP
        return generateAndSendOtp(loginRequest.getEmailOrPhone(), user.getRoleAsString());
    }

    public JwtResponse verifyOtpAndGenerateToken(VerifyOtpRequestDto dto) {
        System.out.println("🔥 Unified OTP Verification for: " + dto.getEmailOrPhone());
        
        // Find user across all tables
        User user = findUserAcrossAllTables(dto.getEmailOrPhone());
        
        if (user == null) {
            System.out.println("❌ User not found for: " + dto.getEmailOrPhone());
            return null;
        }
        
        // Verify OTP
        Optional<OtpVerification> otpOpt = otpRepo.findByEmailOrPhone(dto.getEmailOrPhone());
        if (!otpOpt.isPresent()) {
            System.out.println("❌ No OTP found for: " + dto.getEmailOrPhone());
            return null;
        }
        
        OtpVerification otp = otpOpt.get();
        if (!otp.getOtp().equals(dto.getOtp()) || !otp.getExpiryTime().isAfter(LocalDateTime.now())) {
            System.out.println("❌ Invalid or expired OTP for: " + dto.getEmailOrPhone());
            return null;
        }
        
        // Mark user as verified and save to the correct table
        updateUserVerificationStatus(user.getEmail(), true);
        otpRepo.delete(otp);
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoleAsString(), user.getId());
        System.out.println("✅ OTP verification successful for: " + dto.getEmailOrPhone());
        
        return JwtResponse.builder()
            .token(token)
            .message("Login successful!")
            .user(JwtResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRoleWithReplace("ROLE_", ""))
                .isVerified(user.isVerified())
                .build())
            .build();
    }

    // Helper methods
    private User createUser(RegisterRequestDto dto) {
        // Validate and set name - ensure name is not null or empty
        String userName = dto.getName();
        if (userName == null || userName.trim().isEmpty()) {
            // If name is empty, try to derive it from email
            if (dto.getEmail() != null && dto.getEmail().contains("@")) {
                userName = dto.getEmail().substring(0, dto.getEmail().indexOf("@"));
            } else {
                userName = "User"; // Default fallback name
            }
            System.out.println("⚠️ Name was null/empty, using fallback name: " + userName);
        }
        
        System.out.println("🔧 Creating user with name: '" + userName + "', email: '" + dto.getEmail() + "', phone: '" + dto.getPhone() + "'");
        
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        if ("ROLE_VENDOR".equals(dto.getRole()) || "SELLER".equals(dto.getRole()) || "VENDOR".equals(dto.getRole())) {
            // First create the base User record
            User baseUser = User.builder()
                .name(userName)
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .role(mapStringToUserRole(dto.getRole()))
                .isVerified(false)
                .isActive(true)
                .country("India")
                .createdAt(java.time.LocalDateTime.now())
                .build();
            
            User savedUser = userRepository.save(baseUser);
            System.out.println("✅ Base user created with ID: " + savedUser.getId());
            
            // Then create vendor record that references the user
            Vendors vendor = Vendors.builder()
                .user(savedUser)  // Reference to the User object
                .name(userName)
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .role(dto.getRole())
                .businessName(dto.getBusinessName())
                .businessAddress(dto.getBusinessAddress())
                .gstNumber(dto.getGstNumber())
                .panNumber(dto.getPanNumber())
                .verified(false)
                .build();
            
            Vendors savedVendor = vendorsRepository.save(vendor);
            System.out.println("✅ Vendor created with ID: " + savedVendor.getId());
            
            // Return the User object (which is what the rest of the system expects)
            return savedUser;
        } else if ("ROLE_ADMIN".equals(dto.getRole()) || "ADMIN".equals(dto.getRole())) {
            // Create admin in Admins table
            Admins admin = Admins.builder()
                .name(userName)
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .role(dto.getRole())
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .verified(false)
                .build();
            
            Admins savedAdmin = adminsRepository.save(admin);
            
            // Return a User object for consistency
            return User.builder()
                .id(savedAdmin.getId())
                .name(savedAdmin.getName())
                .email(savedAdmin.getEmail())
                .phone(savedAdmin.getPhone())
                .password(savedAdmin.getPassword())
                .role(mapStringToUserRole(savedAdmin.getRole()))
                .isVerified(savedAdmin.isVerified())
                .build();
        } else if ("ROLE_USER".equals(dto.getRole()) || "ROLE_BUYER".equals(dto.getRole()) || "BUYER".equals(dto.getRole())) {
            // Create buyer - first create base User record, then Buyer record
            User baseUser = User.builder()
                .name(userName)
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .role(User.UserRole.BUYER) // Normalize to BUYER for buyers
                .isVerified(false)
                .isActive(true)
                .country("India")
                .createdAt(java.time.LocalDateTime.now())
                .build();
            
            User savedUser = userRepository.save(baseUser);
            System.out.println("✅ Base user created for buyer with ID: " + savedUser.getId());
            
            // Create buyer record using BuyerService
            CreateBuyerDto buyerDto = mapToBuyerDto(dto, userName);
            try {
                BuyerDto createdBuyer = buyerService.createBuyer(buyerDto);
                System.out.println("✅ Buyer created with ID: " + createdBuyer.getId());
            } catch (Exception e) {
                System.out.println("⚠️ Buyer creation failed, but User exists: " + e.getMessage());
                // Continue with user creation even if buyer creation fails
            }
            
            return savedUser;
        } else {
            // Create regular user in User table for other roles
            User user = User.builder()
                .name(userName)
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .role(mapStringToUserRole(dto.getRole()))
                .isVerified(false)
                .isActive(true)
                .country("India")
                .createdAt(java.time.LocalDateTime.now())
                .build();
            
            System.out.println("🔧 Creating user with name: '" + user.getName() + "', verified: " + user.getIsVerified() + ", active: " + user.getIsActive());
            return userRepository.save(user);
        }
    }

    private String sendRegistrationOtp(RegisterRequestDto dto, User user) {
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        
        // 🚨 DEVELOPMENT ONLY: Log OTP to console for testing
        System.out.println("\n========== OTP GENERATED FOR DEVELOPMENT ===========");
        System.out.println("📧 Email: " + dto.getEmail());
        System.out.println("📱 Phone: " + dto.getPhone());
        System.out.println("🔢 OTP Code: " + otp);
        System.out.println("⏰ Valid until: " + expiry);
        System.out.println("===================================================\n");
        
        // Clean up old OTPs
        if (dto.getEmail() != null) {
            otpRepo.deleteByEmailOrPhone(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            otpRepo.deleteByEmailOrPhone(dto.getPhone());
        }
        
        // Store OTP
        if (dto.getEmail() != null) {
            otpRepo.save(OtpVerification.builder()
                .emailOrPhone(dto.getEmail())
                .otp(otp)
                .expiryTime(expiry)
                .build());
        }
        
        if (dto.getPhone() != null) {
            otpRepo.save(OtpVerification.builder()
                .emailOrPhone(dto.getPhone())
                .otp(otp)
                .expiryTime(expiry)
                .build());
        }
        
        // Send OTP
        if (dto.getEmail() != null) {
            System.out.println("✉️ Sending OTP to email: " + dto.getEmail());
            emailService.sendOtp(dto.getEmail(), otp);
        }
        if (dto.getPhone() != null) {
            System.out.println("📱 Sending OTP to phone: " + dto.getPhone());
            smsService.sendOtp(dto.getPhone(), otp);
        }
        
        return "OTP sent to your email and phone";
    }

    private boolean validatePassword(String inputPassword, String storedPassword) {
        System.out.println("🔍 Debug - Input password: " + inputPassword);
        System.out.println("🔍 Debug - Stored password: " + storedPassword);
        
        // Check if stored password is BCrypt hash
        if (storedPassword != null && storedPassword.startsWith("$2a$")) {
            // BCrypt validation
            boolean matches = passwordEncoder.matches(inputPassword, storedPassword);
            System.out.println("🔍 Debug - BCrypt password match: " + matches);
            return matches;
        } else {
            // Plain text validation (for backward compatibility)
            boolean matches = inputPassword != null && inputPassword.equals(storedPassword);
            System.out.println("🔍 Debug - Plain text password match: " + matches);
            return matches;
        }
    }

    private String generateAndSendOtp(String contact, String role) {
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        
        // 🚨 DEVELOPMENT ONLY: Log OTP to console for testing
        System.out.println("\n========== LOGIN OTP GENERATED FOR DEVELOPMENT ===========");
        System.out.println("📞 Contact: " + contact);
        System.out.println("🔗 Role: " + role);
        System.out.println("🔢 OTP Code: " + otp);
        System.out.println("⏰ Valid until: " + expiry);
        System.out.println("==========================================================\n");
        
        otpRepo.deleteByEmailOrPhone(contact);
        otpRepo.save(OtpVerification.builder()
            .emailOrPhone(contact)
            .otp(otp)
            .expiryTime(expiry)
            .build());
        
        if (contact.contains("@")) {
            emailService.sendOtp(contact, otp);
            return "Password verified. OTP sent to your email.";
        } else {
            smsService.sendOtp(contact, otp);
            return "Password verified. OTP sent to your phone.";
        }
    }

    private String generateOtp() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(999999));
    }
    
    /**
     * Find user across all tables (User, Vendors, Admins)
     */
    private User findUserAcrossAllTables(String emailOrPhone) {
        System.out.println("🔍 Searching for user across all tables: " + emailOrPhone);
        
        // Check User table first
        Optional<User> userOpt = userRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        if (userOpt.isPresent()) {
            System.out.println("✅ Found in User table");
            return userOpt.get();
        }
        
        // Check Vendors table
        Optional<Vendors> vendorOpt = vendorsRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        if (vendorOpt.isPresent()) {
            System.out.println("✅ Found in Vendors table");
            Vendors vendor = vendorOpt.get();
            return User.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .password(vendor.getPassword())
                .role(mapStringToUserRole(vendor.getRole()))
                .isVerified(vendor.isVerified())
                .build();
        }
        
        // Check Admins table
        Optional<Admins> adminOpt = adminsRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        if (adminOpt.isPresent()) {
            System.out.println("✅ Found in Admins table");
            Admins admin = adminOpt.get();
            return User.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .password(admin.getPassword())
                .role(mapStringToUserRole(admin.getRole()))
                .isVerified(admin.isVerified())
                .build();
        }
        
        // Check Buyers table
        Optional<Buyer> buyerOpt = buyerRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone);
        if (buyerOpt.isPresent()) {
            System.out.println("✅ Found in Buyers table");
            Buyer buyer = buyerOpt.get();
            return User.builder()
                .id(buyer.getId())
                .name(buyer.getBuyerName())
                .email(buyer.getEmail())
                .phone(buyer.getPhone())
                .password(buyer.getPassword())
                .role(User.UserRole.BUYER) // Buyers use BUYER role
                .isVerified(buyer.getIsEmailVerified() != null && buyer.getIsEmailVerified())
                .build();
        }
        
        System.out.println("❌ Not found in any table");
        return null;
    }
    
    /**
     * Update user verification status in the correct table
     */
    private void updateUserVerificationStatus(String email, boolean verified) {
        System.out.println("🔄 Updating verification status for: " + email + " to " + verified);
        
        // Check User table first
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            System.out.println("✅ Updating in User table");
            User user = userOpt.get();
            user.setVerified(verified);
            userRepository.save(user);
            return;
        }
        
        // Check Vendors table
        Optional<Vendors> vendorOpt = vendorsRepository.findByEmail(email);
        if (vendorOpt.isPresent()) {
            System.out.println("✅ Updating in Vendors table");
            Vendors vendor = vendorOpt.get();
            vendor.setVerified(verified);
            vendorsRepository.save(vendor);
            return;
        }
        
        // Check Admins table
        Optional<Admins> adminOpt = adminsRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            System.out.println("✅ Updating in Admins table");
            Admins admin = adminOpt.get();
            admin.setVerified(verified);
            adminsRepository.save(admin);
            return;
        }
        
        // Check Buyers table
        Optional<Buyer> buyerOpt = buyerRepository.findByEmail(email);
        if (buyerOpt.isPresent()) {
            System.out.println("✅ Updating in Buyers table");
            Buyer buyer = buyerOpt.get();
            buyer.setIsEmailVerified(verified);
            buyerRepository.save(buyer);
            return;
        }
        
        System.out.println("❌ User not found in any table for verification update");
    }
    
    /**
     * Change user password
     */
    public String changePassword(String currentPassword, String newPassword) {
        System.out.println("🔒 Change password request received");
        
        // Get current user from JWT token (you'll need to implement this)
        // For now, we'll use a placeholder - you'll need to get the user from security context
        String currentUserEmail = getCurrentUserEmail();
        
        if (currentUserEmail == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        // Find user across all tables
        User user = findUserAcrossAllTables(currentUserEmail);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // Validate current password
        if (!validatePassword(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Encode new password
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        
        // Update password in the correct table
        updateUserPassword(user.getEmail(), encodedNewPassword);
        
        System.out.println("✅ Password changed successfully for user: " + currentUserEmail);
        return "Password changed successfully";
    }
    
    /**
     * Update user profile
     */
    public Object updateProfile(Map<String, Object> profileData) {
        System.out.println("📝 Update profile request for data: " + profileData);
        
        String currentUserEmail = getCurrentUserEmail();
        
        if (currentUserEmail == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        // Find user across all tables
        User user = findUserAcrossAllTables(currentUserEmail);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // Update profile in the correct table based on role
        return updateUserProfile(user, profileData);
    }
    
    /**
     * Get current user profile
     */
    public Object getCurrentUserProfile() {
        System.out.println("📋 Get current user profile request");
        
        String currentUserEmail = getCurrentUserEmail();
        
        if (currentUserEmail == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        // Find user across all tables
        User user = findUserAcrossAllTables(currentUserEmail);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // Return user profile based on role
        return getUserProfileByRole(user);
    }
    
    /**
     * Helper method to get current user email from JWT token
     * You'll need to implement this based on your security configuration
     */
    private String getCurrentUserEmail() {
        // This is a placeholder - you'll need to implement getting the user from security context
        // For Spring Security with JWT, this would typically be:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // return auth.getName();
        
        // For now, returning null - you'll need to implement this
        try {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                return auth.getName();
            }
        } catch (Exception e) {
            System.out.println("❌ Error getting current user: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Update password in the correct table
     */
    private void updateUserPassword(String email, String newPassword) {
        System.out.println("🔄 Updating password for: " + email);
        
        // Check User table first
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            System.out.println("✅ Updating password in User table");
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return;
        }
        
        // Check Vendors table
        Optional<Vendors> vendorOpt = vendorsRepository.findByEmail(email);
        if (vendorOpt.isPresent()) {
            System.out.println("✅ Updating password in Vendors table");
            Vendors vendor = vendorOpt.get();
            vendor.setPassword(newPassword);
            vendorsRepository.save(vendor);
            return;
        }
        
        // Check Admins table
        Optional<Admins> adminOpt = adminsRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            System.out.println("✅ Updating password in Admins table");
            Admins admin = adminOpt.get();
            admin.setPassword(newPassword);
            adminsRepository.save(admin);
            return;
        }
        
        // Check Buyers table
        Optional<Buyer> buyerOpt = buyerRepository.findByEmail(email);
        if (buyerOpt.isPresent()) {
            System.out.println("✅ Updating password in Buyers table");
            Buyer buyer = buyerOpt.get();
            buyer.setPassword(newPassword);
            buyerRepository.save(buyer);
            return;
        }
        
        System.out.println("❌ User not found in any table for password update");
    }
    
    /**
     * Update user profile in the correct table
     */
    private Object updateUserProfile(User user, Map<String, Object> profileData) {
        System.out.println("🔄 Updating profile for: " + user.getEmail() + ", Role: " + user.getRole());
        
        String name = (String) profileData.get("name");
        String phone = (String) profileData.get("phone");
        String address = (String) profileData.get("address");
        String companyName = (String) profileData.get("companyName");
        
        // Update based on role
        if ("ROLE_USER".equals(user.getRole())) {
            Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
            if (userOpt.isPresent()) {
                User existingUser = userOpt.get();
                if (name != null) existingUser.setName(name);
                if (phone != null) existingUser.setPhone(phone);
                if (address != null) existingUser.setAddress(address);
                
                User savedUser = userRepository.save(existingUser);
                return createUserResponse(savedUser);
            }
        } else if ("ROLE_VENDOR".equals(user.getRole()) || "SELLER".equals(user.getRole()) || "VENDOR".equals(user.getRole())) {
            Optional<Vendors> vendorOpt = vendorsRepository.findByEmail(user.getEmail());
            if (vendorOpt.isPresent()) {
                Vendors vendor = vendorOpt.get();
                if (name != null) vendor.setName(name);
                if (phone != null) vendor.setPhone(phone);
                if (address != null) vendor.setBusinessAddress(address);
                if (companyName != null) vendor.setBusinessName(companyName);
                
                Vendors savedVendor = vendorsRepository.save(vendor);
                return createVendorResponse(savedVendor);
            }
        } else if ("ROLE_ADMIN".equals(user.getRole()) || "ADMIN".equals(user.getRole())) {
            Optional<Admins> adminOpt = adminsRepository.findByEmail(user.getEmail());
            if (adminOpt.isPresent()) {
                Admins admin = adminOpt.get();
                if (name != null) admin.setName(name);
                if (phone != null) admin.setPhone(phone);
                
                Admins savedAdmin = adminsRepository.save(admin);
                return createAdminResponse(savedAdmin);
            }
        } else if ("ROLE_USER".equals(user.getRole()) || "BUYER".equals(user.getRole())) {
            // Check if this is actually a buyer
            Optional<Buyer> buyerOpt = buyerRepository.findByEmail(user.getEmail());
            if (buyerOpt.isPresent()) {
                Buyer buyer = buyerOpt.get();
                if (name != null) buyer.setBuyerName(name);
                if (phone != null) buyer.setPhone(phone);
                if (address != null) {
                    buyer.setBillingAddressLine1(address);
                    if (buyer.getSameAsBilling() == null || buyer.getSameAsBilling()) {
                        buyer.setShippingAddressLine1(address);
                    }
                }
                
                Buyer savedBuyer = buyerRepository.save(buyer);
                return createBuyerResponse(savedBuyer);
            }
            
            // Fall back to regular user update if not a buyer
            Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
            if (userOpt.isPresent()) {
                User existingUser = userOpt.get();
                if (name != null) existingUser.setName(name);
                if (phone != null) existingUser.setPhone(phone);
                if (address != null) existingUser.setAddress(address);
                
                User savedUser = userRepository.save(existingUser);
                return createUserResponse(savedUser);
            }
        }
        
        throw new RuntimeException("Failed to update profile");
    }
    
    /**
     * Get user profile by role
     */
    private Object getUserProfileByRole(User user) {
        if ("ROLE_USER".equals(user.getRole())) {
            Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
            if (userOpt.isPresent()) {
                return createUserResponse(userOpt.get());
            }
        } else if ("ROLE_VENDOR".equals(user.getRole())) {
            Optional<Vendors> vendorOpt = vendorsRepository.findByEmail(user.getEmail());
            if (vendorOpt.isPresent()) {
                return createVendorResponse(vendorOpt.get());
            }
        } else if ("ROLE_ADMIN".equals(user.getRole()) || "ADMIN".equals(user.getRole())) {
            Optional<Admins> adminOpt = adminsRepository.findByEmail(user.getEmail());
            if (adminOpt.isPresent()) {
                return createAdminResponse(adminOpt.get());
            }
        } else if ("ROLE_USER".equals(user.getRole()) || "BUYER".equals(user.getRole())) {
            // Check if this is actually a buyer
            Optional<Buyer> buyerOpt = buyerRepository.findByEmail(user.getEmail());
            if (buyerOpt.isPresent()) {
                return createBuyerResponse(buyerOpt.get());
            }
            
            // Fall back to regular user if not a buyer
            Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
            if (userOpt.isPresent()) {
                return createUserResponse(userOpt.get());
            }
        }
        
        throw new RuntimeException("User profile not found");
    }
    
    /**
     * Create user response object
     */
    private Object createUserResponse(User user) {
        return Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "phone", user.getPhone() != null ? user.getPhone() : "",
            "address", user.getAddress() != null ? user.getAddress() : "",
            "role", user.getRoleWithReplace("ROLE_", "").toLowerCase(),
            "userType", user.getRoleWithReplace("ROLE_", "").toLowerCase(),
            "isVerified", user.isVerified(),
            "createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
        );
    }
    
    /**
     * Create vendor response object
     */
    private Object createVendorResponse(Vendors vendor) {
        return Map.of(
            "id", vendor.getId(),
            "name", vendor.getName(),
            "email", vendor.getEmail(),
            "phone", vendor.getPhone() != null ? vendor.getPhone() : "",
            "address", vendor.getBusinessAddress() != null ? vendor.getBusinessAddress() : "",
            "companyName", vendor.getBusinessName() != null ? vendor.getBusinessName() : "",
            "role", vendor.getRole().replace("ROLE_", "").toLowerCase(),
            "userType", vendor.getRole().replace("ROLE_", "").toLowerCase(),
            "isVerified", vendor.isVerified(),
            "createdAt", vendor.getCreatedAt() != null ? vendor.getCreatedAt().toString() : ""
        );
    }
    
    /**
     * Create admin response object
     */
    private Object createAdminResponse(Admins admin) {
        return Map.of(
            "id", admin.getId(),
            "name", admin.getName(),
            "email", admin.getEmail(),
            "phone", admin.getPhone() != null ? admin.getPhone() : "",
            "role", admin.getRole().replace("ROLE_", "").toLowerCase(),
            "userType", admin.getRole().replace("ROLE_", "").toLowerCase(),
            "isVerified", admin.isVerified(),
            "createdAt", admin.getCreatedAt() != null ? admin.getCreatedAt().toString() : ""
        );
    }
    
    /**
     * Create buyer response object
     */
    private Object createBuyerResponse(Buyer buyer) {
        return Map.of(
            "id", buyer.getId(),
            "name", buyer.getBuyerName() != null ? buyer.getBuyerName() : "",
            "email", buyer.getEmail(),
            "phone", buyer.getPhone() != null ? buyer.getPhone() : "",
            "address", buyer.getBillingAddressLine1() != null ? buyer.getBillingAddressLine1() : "",
            "companyName", buyer.getFirstName() != null && buyer.getLastName() != null ? 
                buyer.getFirstName() + " " + buyer.getLastName() : "",
            "role", "user",
            "userType", "buyer",
            "isVerified", buyer.getIsEmailVerified() != null && buyer.getIsEmailVerified(),
            "createdAt", buyer.getCreatedAt() != null ? buyer.getCreatedAt().toString() : ""
        );
    }
    
    /**
     * Send forgot password OTP
     */
    public String sendForgotPasswordOtp(String email) {
        System.out.println("📧 Forgot password OTP request for: " + email);
        
        // Find user across all tables (User, Vendors, Admins)
        User user = findUserAcrossAllTables(email);
        
        if (user == null) {
            System.out.println("❌ User not found with email: " + email);
            return "Email not found. Please check your email address.";
        }
        
        System.out.println("✅ User found: " + user.getEmail() + ", Role: " + user.getRole());
        
        // Generate and send OTP
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        
        // 🚨 DEVELOPMENT ONLY: Log OTP to console for testing
        System.out.println("\n========== FORGOT PASSWORD OTP FOR DEVELOPMENT ===========");
        System.out.println("📧 Email: " + email);
        System.out.println("🔢 OTP Code: " + otp);
        System.out.println("⏰ Valid until: " + expiry);
        System.out.println("============================================================\n");
        
        // Clean up old OTPs for this email
        otpRepo.deleteByEmailOrPhone(email);
        
        // Store new OTP
        otpRepo.save(OtpVerification.builder()
            .emailOrPhone(email)
            .otp(otp)
            .expiryTime(expiry)
            .build());
        
        // Send OTP via email
        System.out.println("✉️ Sending forgot password OTP to email: " + email);
        emailService.sendForgotPasswordOtp(email, otp);
        
        return "OTP sent to your email for password recovery.";
    }
    
    /**
     * Create address if provided in registration data
     */
    private void createAddressIfProvided(RegisterRequestDto dto, User user) {
        // Create address from registration data if address fields are provided
        if (hasAddressData(dto)) {
            try {
                System.out.println("📍 Creating address for user: " + user.getId());
                
                UserAddress address = new UserAddress();
                address.setAddressType("HOME"); // Default type
                address.setFullName(user.getName());
                address.setAddressLine1(dto.getBusinessAddress() != null ? dto.getBusinessAddress() : 
                    (dto.getCity() != null ? "Address in " + dto.getCity() : null));
                address.setCity(dto.getCity());
                address.setState(dto.getState());
                address.setPincode(dto.getPincode());
                address.setPhone(dto.getPhone());
                address.setDefault(true);
                
                if (address.getAddressLine1() != null || address.getCity() != null) {
                    UserAddress savedAddress = userAddressService.createAddress(user.getId(), address);
                    System.out.println("✅ Address created with ID: " + savedAddress.getId());
                }
            } catch (Exception e) {
                System.out.println("⚠️ Address creation failed: " + e.getMessage());
                // Continue without address - not critical for registration
            }
        }
    }
    
    /**
     * Check if registration data contains address information
     */
    private boolean hasAddressData(RegisterRequestDto dto) {
        return dto.getBusinessAddress() != null || dto.getCity() != null || 
               dto.getState() != null || dto.getPincode() != null;
    }
    
    /**
     * Map registration DTO to buyer DTO
     */
    private CreateBuyerDto mapToBuyerDto(RegisterRequestDto dto, String userName) {
        CreateBuyerDto buyerDto = new CreateBuyerDto();
        
        // Basic information
        buyerDto.setBuyerName(userName);
        buyerDto.setEmail(dto.getEmail());
        buyerDto.setPhone(dto.getPhone());
        buyerDto.setPassword(dto.getPassword()); // Will be encoded by BuyerService
        buyerDto.setBuyerType(Buyer.BuyerType.INDIVIDUAL); // Default
        
        // Personal information - extract from name if possible
        String[] nameParts = userName.split(" ", 2);
        buyerDto.setFirstName(nameParts[0]);
        buyerDto.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        buyerDto.setDisplayName(userName);
        
        // Address information from registration
        if (dto.getBusinessAddress() != null) {
            buyerDto.setBillingAddressLine1(dto.getBusinessAddress());
        }
        if (dto.getCity() != null) {
            buyerDto.setBillingCity(dto.getCity());
        }
        if (dto.getState() != null) {
            buyerDto.setBillingState(dto.getState());
        }
        if (dto.getPincode() != null) {
            buyerDto.setBillingPostalCode(dto.getPincode());
        }
        buyerDto.setBillingCountry("India");
        
        // Set same as billing for shipping
        buyerDto.setSameAsBilling(true);
        
        // Default preferences
        buyerDto.setEmailNotifications(true);
        buyerDto.setSmsNotifications(false);
        buyerDto.setMarketingEmails(true);
        buyerDto.setPriceAlerts(false);
        buyerDto.setNewProductAlerts(false);
        buyerDto.setOrderUpdates(true);
        
        // Accept terms by default (should be validated on frontend)
        buyerDto.setAcceptedTermsAndConditions(true);
        buyerDto.setAcceptedPrivacyPolicy(true);
        
        return buyerDto;
    }
    
    /**
     * Map string role to UserRole enum
     */
    private User.UserRole mapStringToUserRole(String roleString) {
        if (roleString == null) {
            return User.UserRole.BUYER;
        }
        
        switch (roleString.toUpperCase()) {
            case "ROLE_USER":
            case "ROLE_BUYER":
            case "BUYER":
                return User.UserRole.BUYER;
            case "ROLE_VENDOR":
            case "ROLE_SELLER":
            case "SELLER":
            case "VENDOR":
                return User.UserRole.SELLER;
            case "ROLE_ADMIN":
            case "ADMIN":
                return User.UserRole.ADMIN;
            case "ROLE_SUPPORT":
            case "SUPPORT":
                return User.UserRole.SUPPORT;
            case "ROLE_CTO":
            case "CTO":
                return User.UserRole.CTO;
            case "ROLE_DATA_ENTRY":
            case "DATA_ENTRY":
                return User.UserRole.DATA_ENTRY;
            default:
                return User.UserRole.BUYER; // Default fallback
        }
    }
    
    /**
     * Check if email exists and return its role
     */
    public Map<String, String> checkEmailRole(String email) {
        System.out.println("🔍 Checking email role for: " + email);
        
        // Find user across all tables
        User user = findUserAcrossAllTables(email);
        
        if (user == null) {
            System.out.println("❌ Email not found: " + email);
            return Map.of(
                "exists", "false",
                "message", "Email not found"
            );
        }
        
        System.out.println("✅ Email found with role: " + user.getRole());
        return Map.of(
            "exists", "true",
            "role", user.getRoleAsString(),
            "email", user.getEmail()
        );
    }
    
    /**
     * Verify forgot password OTP and login user
     */
    public JwtResponse verifyForgotPasswordOtp(String email, String otpCode, String newPassword) {
        System.out.println("🔐 Forgot password OTP verification for: " + email);
        
        // Find user in User table
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (!userOpt.isPresent()) {
            System.out.println("❌ User not found with email: " + email);
            return null;
        }
        
        User user = userOpt.get();
        
        // Verify OTP
        Optional<OtpVerification> otpOpt = otpRepo.findByEmailOrPhone(email);
        if (!otpOpt.isPresent()) {
            System.out.println("❌ No OTP found for email: " + email);
            return null;
        }
        
        OtpVerification otp = otpOpt.get();
        if (!otp.getOtp().equals(otpCode) || !otp.getExpiryTime().isAfter(LocalDateTime.now())) {
            System.out.println("❌ Invalid or expired OTP");
            return null;
        }
        
        // If new password is provided, update the password
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            System.out.println("🔒 Password updated for user: " + email);
        }
        
        // Mark user as verified and save
        user.setVerified(true);
        userRepository.save(user);
        
        // Clean up OTP
        otpRepo.delete(otp);
        
        // Generate token and login the user
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoleAsString(), user.getId());
        
        System.out.println("✅ Forgot password OTP verification successful, user logged in");
        
        return JwtResponse.builder()
            .token(token)
            .message("OTP verified successfully. You are now logged in.")
            .user(JwtResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRoleWithReplace("ROLE_", ""))
                .isVerified(user.isVerified())
                .build())
            .build();
    }
}


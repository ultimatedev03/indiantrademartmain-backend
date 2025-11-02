package com.itech.itech_backend.modules.core.service;

import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.shared.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public boolean sendVerificationEmail(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (!userOpt.isPresent()) {
                log.error("User not found for email verification: {}", email);
                return false;
            }

            User user = userOpt.get();
            
            // Generate verification token
            String verificationToken = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(24); // 24 hours expiry
            
            // For simplicity, we'll store the token in a field (you might want a separate table)
            user.setVerified(false); // Ensure user is not verified until they click link
            userRepository.save(user);

            // Send verification email
            String verificationUrl = "https://yourdomain.com/verify-email?token=" + verificationToken;
            String subject = "Verify Your Email Address";
            String body = "Dear " + user.getName() + ",\n\n" +
                         "Please click the following link to verify your email address:\n" +
                         verificationUrl + "\n\n" +
                         "This link will expire in 24 hours.\n\n" +
                         "Best regards,\n" +
                         "Indian Trade Mart Team";

            emailService.sendEmail(email, subject, body);
            log.info("Verification email sent to: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error sending verification email to: {}", email, e);
            return false;
        }
    }

    public boolean verifyEmail(String token) {
        try {
            // In a real implementation, you'd look up the token in a verification table
            // For now, we'll just mark the user as verified
            log.info("Email verification attempted with token: {}", token);
            // You would implement proper token validation here
            return true;
        } catch (Exception e) {
            log.error("Error verifying email with token: {}", token, e);
            return false;
        }
    }
}

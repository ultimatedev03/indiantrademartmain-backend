package com.itech.itech_backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Test Configuration Class
 * Provides common configuration for all tests
 */
@TestConfiguration
@ActiveProfiles("test")
@AutoConfigureWebMvc
public class TestConfig {
    
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Primary
    public ObjectMapper testObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    /**
     * Test utilities and helper methods
     */
    public static class TestUtils {
        
        public static final String TEST_ADMIN_EMAIL = "admin@test.com";
        public static final String TEST_VENDOR_EMAIL = "vendor@test.com";
        public static final String TEST_BUYER_EMAIL = "buyer@test.com";
        public static final String TEST_EMPLOYEE_EMAIL = "employee@test.com";
        public static final String TEST_PASSWORD = "testpass123";
        
        public static final String TEST_JWT_SECRET = "test-secret-key-for-automated-testing-suite-2024";
        
        public static final Long TEST_ADMIN_ID = 1L;
        public static final Long TEST_VENDOR_ID = 2L;
        public static final Long TEST_BUYER_ID = 3L;
        public static final Long TEST_EMPLOYEE_ID = 4L;
        
        // Test data constants
        public static final String TEST_PRODUCT_SKU = "TSP001";
        public static final String TEST_ORDER_NUMBER = "ORD001";
        public static final String TEST_COMPANY_GSTIN = "29ABCDE1234F1Z5";
        
        /**
         * Generate test JWT token for authentication
         */
        public static String generateTestJWT(String userEmail, String userRole) {
            // This would use your actual JWT service in tests
            return "test-jwt-token-" + userEmail + "-" + userRole;
        }
        
        /**
         * Create test request headers with authentication
         */
        public static org.springframework.http.HttpHeaders createAuthHeaders(String token) {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            return headers;
        }
    }
}

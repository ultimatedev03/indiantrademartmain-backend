package com.itech.itech_backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.itech_backend.TestConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Integration Tests for Authentication Controller
 * Tests all authentication-related endpoints
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
@DisplayName("Authentication Controller Integration Tests")
public class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }
    
    @Test
    @DisplayName("Should register new vendor successfully")
    void testVendorRegistration() throws Exception {
        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("email", "newvendor@test.com");
        registrationData.put("password", "securepass123");
        registrationData.put("firstName", "New");
        registrationData.put("lastName", "Vendor");
        registrationData.put("phone", "+91-9876543210");
        registrationData.put("userType", "VENDOR");
        
        // Company details
        Map<String, Object> company = new HashMap<>();
        company.put("name", "New Test Vendor Company");
        company.put("gstin", "29NEWCO1234F1Z5");
        company.put("pan", "NEWCO1234F");
        company.put("address", "New Test Street");
        company.put("city", "Pune");
        company.put("state", "Maharashtra");
        company.put("country", "India");
        company.put("pincode", "411001");
        
        registrationData.put("company", company);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.email").value("newvendor@test.com"));
    }
    
    @Test
    @DisplayName("Should login vendor successfully")
    void testVendorLogin() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", TestConfig.TestUtils.TEST_VENDOR_EMAIL);
        loginData.put("password", TestConfig.TestUtils.TEST_PASSWORD);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(TestConfig.TestUtils.TEST_VENDOR_EMAIL))
                .andExpect(jsonPath("$.user.userType").value("VENDOR"));
    }
    
    @Test
    @DisplayName("Should login buyer successfully")
    void testBuyerLogin() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", TestConfig.TestUtils.TEST_BUYER_EMAIL);
        loginData.put("password", TestConfig.TestUtils.TEST_PASSWORD);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(TestConfig.TestUtils.TEST_BUYER_EMAIL))
                .andExpect(jsonPath("$.user.userType").value("BUYER"));
    }
    
    @Test
    @DisplayName("Should login admin successfully")
    void testAdminLogin() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", TestConfig.TestUtils.TEST_ADMIN_EMAIL);
        loginData.put("password", TestConfig.TestUtils.TEST_PASSWORD);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(TestConfig.TestUtils.TEST_ADMIN_EMAIL))
                .andExpect(jsonPath("$.user.userType").value("ADMIN"));
    }
    
    @Test
    @DisplayName("Should fail login with invalid credentials")
    void testInvalidLogin() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "invalid@test.com");
        loginData.put("password", "wrongpassword");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should validate JWT token successfully")
    void testTokenValidation() throws Exception {
        // First login to get a token
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", TestConfig.TestUtils.TEST_VENDOR_EMAIL);
        loginData.put("password", TestConfig.TestUtils.TEST_PASSWORD);
        
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
                
        // Extract token from response (you'll need to parse JSON)
        // Then test protected endpoint
        mockMvc.perform(get("/api/auth/profile")
                .header("Authorization", "Bearer " + TestConfig.TestUtils.generateTestJWT(TestConfig.TestUtils.TEST_VENDOR_EMAIL, "VENDOR")))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("Should register buyer successfully")
    void testBuyerRegistration() throws Exception {
        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("email", "newbuyer@test.com");
        registrationData.put("password", "securepass123");
        registrationData.put("firstName", "New");
        registrationData.put("lastName", "Buyer");
        registrationData.put("phone", "+91-8765432109");
        registrationData.put("userType", "BUYER");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.email").value("newbuyer@test.com"));
    }
    
    @Test
    @DisplayName("Should fail registration with duplicate email")
    void testDuplicateEmailRegistration() throws Exception {
        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("email", TestConfig.TestUtils.TEST_VENDOR_EMAIL); // Existing email
        registrationData.put("password", "securepass123");
        registrationData.put("firstName", "Duplicate");
        registrationData.put("lastName", "User");
        registrationData.put("phone", "+91-1111111111");
        registrationData.put("userType", "VENDOR");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
    
    @Test
    @DisplayName("Should handle logout successfully")
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + TestConfig.TestUtils.generateTestJWT(TestConfig.TestUtils.TEST_VENDOR_EMAIL, "VENDOR")))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

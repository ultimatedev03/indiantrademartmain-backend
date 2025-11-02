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
 * Integration Tests for Vendor Dashboard Functionality
 * Tests vendor-related operations like product management, orders, analytics
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
@DisplayName("Vendor Dashboard Integration Tests")
public class VendorDashboardIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    private String vendorToken;
    
    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        
        // Get vendor token for authenticated requests
        vendorToken = TestConfig.TestUtils.generateTestJWT(
            TestConfig.TestUtils.TEST_VENDOR_EMAIL, 
            "VENDOR"
        );
    }
    
    @Test
    @DisplayName("Should get vendor dashboard analytics")
    void testVendorDashboardAnalytics() throws Exception {
        mockMvc.perform(get("/api/vendor/dashboard/analytics")
                .header("Authorization", "Bearer " + vendorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").exists())
                .andExpect(jsonPath("$.totalOrders").exists())
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.pendingOrders").exists());
    }
    
    @Test
    @DisplayName("Should get vendor products list")
    void testGetVendorProducts() throws Exception {
        mockMvc.perform(get("/api/vendor/products")
                .header("Authorization", "Bearer " + vendorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].sku").exists());
    }
    
    @Test
    @DisplayName("Should create new product successfully")
    void testCreateProduct() throws Exception {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "New Test Product");
        productData.put("description", "A comprehensive description of the new test product");
        productData.put("shortDescription", "Short description for test product");
        productData.put("price", 15000.00);
        productData.put("discountPrice", 14000.00);
        productData.put("sku", "NTP001");
        productData.put("stockQuantity", 25);
        productData.put("minOrderQuantity", 1);
        productData.put("categoryId", 1);
        productData.put("isActive", true);
        productData.put("isFeatured", false);
        
        mockMvc.perform(post("/api/vendor/products")
                .header("Authorization", "Bearer " + vendorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("New Test Product"))
                .andExpect(jsonPath("$.data.sku").value("NTP001"));
    }
    
    @Test
    @DisplayName("Should update existing product successfully")
    void testUpdateProduct() throws Exception {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "Updated Test Smartphone");
        updateData.put("description", "Updated description for test smartphone");
        updateData.put("price", 26000.00);
        updateData.put("discountPrice", 24000.00);
        updateData.put("stockQuantity", 120);
        
        mockMvc.perform(put("/api/vendor/products/1")
                .header("Authorization", "Bearer " + vendorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Test Smartphone"))
                .andExpect(jsonPath("$.data.price").value(26000.00));
    }
    
    @Test
    @DisplayName("Should get vendor orders list")
    void testGetVendorOrders() throws Exception {
        mockMvc.perform(get("/api/vendor/orders")
                .header("Authorization", "Bearer " + vendorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderNumber").exists())
                .andExpect(jsonPath("$.data[0].status").exists());
    }
    
    @Test
    @DisplayName("Should update order status")
    void testUpdateOrderStatus() throws Exception {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "CONFIRMED");
        statusUpdate.put("notes", "Order confirmed and will be processed soon");
        
        mockMvc.perform(patch("/api/vendor/orders/1/status")
                .header("Authorization", "Bearer " + vendorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }
    
    @Test
    @DisplayName("Should get vendor inquiries")
    void testGetVendorInquiries() throws Exception {
        mockMvc.perform(get("/api/vendor/inquiries")
                .header("Authorization", "Bearer " + vendorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].subject").exists())
                .andExpect(jsonPath("$.data[0].status").exists());
    }
    
    @Test
    @DisplayName("Should respond to inquiry with quote")
    void testRespondToInquiry() throws Exception {
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("quotedPrice", 2100000.00);
        quoteData.put("quantity", 100);
        quoteData.put("validityDays", 30);
        quoteData.put("termsConditions", "Updated terms and conditions for bulk order");
        quoteData.put("notes", "Special pricing for bulk order of 100 units");
        
        mockMvc.perform(post("/api/vendor/inquiries/1/quote")
                .header("Authorization", "Bearer " + vendorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quoteData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.quotedPrice").value(2100000.00))
                .andExpect(jsonPath("$.data.status").value("SENT"));
    }
    
    @Test
    @DisplayName("Should get vendor company profile")
    void testGetVendorCompanyProfile() throws Exception {
        mockMvc.perform(get("/api/vendor/company/profile")
                .header("Authorization", "Bearer " + vendorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.gstin").exists())
                .andExpect(jsonPath("$.data.isVerified").exists());
    }
    
    @Test
    @DisplayName("Should update vendor company profile")
    void testUpdateCompanyProfile() throws Exception {
        Map<String, Object> companyUpdate = new HashMap<>();
        companyUpdate.put("description", "Updated company description for better visibility");
        companyUpdate.put("website", "https://updated-testvendor.com");
        companyUpdate.put("phone", "+91-8888888899");
        
        mockMvc.perform(put("/api/vendor/company/profile")
                .header("Authorization", "Bearer " + vendorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.website").value("https://updated-testvendor.com"));
    }
    
    @Test
    @DisplayName("Should get vendor analytics data")
    void testGetVendorAnalytics() throws Exception {
        mockMvc.perform(get("/api/vendor/analytics")
                .param("period", "last30days")
                .header("Authorization", "Bearer " + vendorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.revenue").exists())
                .andExpect(jsonPath("$.orderCount").exists())
                .andExpect(jsonPath("$.topProducts").exists());
    }
    
    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/vendor/products/2")
                .header("Authorization", "Bearer " + vendorToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }
    
    @Test
    @DisplayName("Should handle unauthorized access properly")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/vendor/products"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should validate product creation with invalid data")
    void testInvalidProductCreation() throws Exception {
        Map<String, Object> invalidProductData = new HashMap<>();
        invalidProductData.put("name", ""); // Empty name should fail validation
        invalidProductData.put("price", -100.00); // Negative price should fail
        
        mockMvc.perform(post("/api/vendor/products")
                .header("Authorization", "Bearer " + vendorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductData)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

package com.itech.itech_backend.modules.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @JsonProperty(value = "name")
    private String name;
    
    @JsonProperty(value = "email")
    private String email;
    
    @JsonProperty(value = "phone")
    private String phone;
    
    @JsonProperty(value = "password")
    private String password;
    
    @JsonProperty(value = "role")
    private String role = "ROLE_USER"; // Default role
    
    @JsonProperty(value = "userType")
    private String userType = "user"; // Default user type
    
    // Vendor-specific fields
    private String businessName;
    private String businessAddress;
    private String city;
    private String state;
    private String pincode;
    private String gstNumber;
    private String panNumber;
    
    // Admin-specific fields
    private String department;
    private String designation;
}


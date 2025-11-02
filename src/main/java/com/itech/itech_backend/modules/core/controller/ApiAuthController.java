package com.itech.itech_backend.modules.core.controller;

import com.itech.itech_backend.modules.shared.dto.RegisterRequestDto;
import com.itech.itech_backend.modules.core.service.UnifiedAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UnifiedAuthService unifiedAuthService;

    @PostMapping("/register1")
    public String register1(@RequestBody RegisterRequestDto dto) {
        System.out.println("🔍 API Registration1 request received (API endpoint):");
        System.out.println("🔍 Name: '" + dto.getName() + "'");
        System.out.println("🔍 Email: '" + dto.getEmail() + "'");
        System.out.println("🔍 Phone: '" + dto.getPhone() + "'");
        System.out.println("🔍 Password: " + (dto.getPassword() != null ? "[PROVIDED]" : "[NULL]"));
        
        // Validate required fields
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            return "Email is required";
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            return "Password is required";
        }
        
        dto.setRole("ROLE_USER");
        dto.setUserType("user");
        
        try {
            return unifiedAuthService.register(dto);
        } catch (RuntimeException e) {
            if ("EMAIL_ALREADY_EXISTS".equals(e.getMessage())) {
                System.out.println("❌ API Registration1 failed - email already exists: " + dto.getEmail());
                return "This email is already registered. Please login instead or use a different email address.";
            }
            // Re-throw other exceptions
            throw e;
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDto dto) {
        System.out.println("🔍 API Registration request received:");
        System.out.println("🔍 Name: '" + dto.getName() + "'");
        System.out.println("🔍 Email: '" + dto.getEmail() + "'");
        System.out.println("🔍 Phone: '" + dto.getPhone() + "'");
        System.out.println("🔍 Password: " + (dto.getPassword() != null ? "[PROVIDED]" : "[NULL]"));
        
        // Validate required fields
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            return "Email is required";
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            return "Password is required";
        }
        
        dto.setRole("ROLE_USER");
        dto.setUserType("user");
        
        try {
            return unifiedAuthService.register(dto);
        } catch (RuntimeException e) {
            if ("EMAIL_ALREADY_EXISTS".equals(e.getMessage())) {
                System.out.println("❌ API Registration failed - email already exists: " + dto.getEmail());
                return "This email is already registered. Please login instead or use a different email address.";
            }
            // Re-throw other exceptions
            throw e;
        }
    }
}
package com.itech.itech_backend.modules.shared.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotRequestDto {
    
    private String message;
    private String sessionId; // To maintain conversation context
    private String userIp;    // Optional: for tracking
    
    // Optional: if user is logged in
    private Long userId;
    
    // User role for role-based responses (NON_LOGGED, BUYER, VENDOR, ADMIN)
    private String userRole;
}


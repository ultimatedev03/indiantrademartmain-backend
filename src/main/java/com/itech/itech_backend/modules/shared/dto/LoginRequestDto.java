package com.itech.itech_backend.modules.shared.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String emailOrPhone;
    private String password;
    private String adminCode; // For admin authentication
}


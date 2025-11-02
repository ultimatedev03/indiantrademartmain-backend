package com.itech.itech_backend.modules.shared.dto;

import lombok.Data;

@Data
public class VerifyOtpRequestDto {
    private String emailOrPhone;
    private String otp;
}


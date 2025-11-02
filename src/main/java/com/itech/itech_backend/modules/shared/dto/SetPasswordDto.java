package com.itech.itech_backend.modules.shared.dto;

import lombok.Data;

@Data
public class SetPasswordDto {
    private String emailOrPhone;
    private String newPassword;
}


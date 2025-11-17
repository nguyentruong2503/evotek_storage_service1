package com.example.iam2.model.dto;

import lombok.Data;

@Data
public class PasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}

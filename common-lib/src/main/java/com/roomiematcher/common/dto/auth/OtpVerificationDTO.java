package com.roomiematcher.common.dto.auth;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationDTO {
    private String email;
    private String otp;
    private LocalDateTime expiryTime;
    private String type; // EMAIL_VERIFICATION, PASSWORD_RESET, etc.
} 
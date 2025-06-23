package com.roomiematcher.auth.client;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${services.notification-service.url}")
public interface NotificationClient {

    @PostMapping("/send-email")
    ApiResponse<Void> sendEmail(@RequestBody EmailRequestDTO emailRequest);
    
    @PostMapping("/send-verification-email")
    ApiResponse<String> sendVerificationEmail(@RequestBody EmailRequestDTO emailRequest);
    
    @PostMapping("/send-password-reset")
    ApiResponse<String> sendPasswordResetEmail(@RequestBody EmailRequestDTO emailRequest);
} 
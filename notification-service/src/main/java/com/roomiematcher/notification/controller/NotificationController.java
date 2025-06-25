package com.roomiematcher.notification.controller;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import com.roomiematcher.notification.service.AwsSesEmailService;
import com.roomiematcher.notification.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Management", description = "APIs for sending email notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final EmailService emailService;
    private final boolean isAwsSes;
    
    public NotificationController(
            EmailService emailService,
            @Value("${notification.provider:standard}") String provider) {
        this.emailService = emailService;
        this.isAwsSes = "aws-ses".equals(provider);
    }

    @PostMapping("/email")
    @Operation(summary = "Send an email notification")
    public ResponseEntity<ApiResponse<String>> sendEmailNotification(
            @Valid @RequestBody EmailRequestDTO emailRequest) {
        logger.debug("Received email request to: {}", emailRequest.getTo());
        
        try {
            emailService.sendEmail(emailRequest);
            return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to send email: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify-email")
    @Operation(summary = "Verify an email address with AWS SES (only works when using aws-ses provider)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyEmail(@RequestParam String email) {
        if (!isAwsSes) {
            Map<String, Object> result = new HashMap<>();
            result.put("email", email);
            result.put("provider", "standard");
            return ResponseEntity.ok(ApiResponse.success("Not using AWS SES provider, verification not required", result));
        }
        
        try {
            AwsSesEmailService sesService = (AwsSesEmailService) emailService;
            boolean sent = sesService.verifyEmailAddress(email);
            
            Map<String, Object> result = new HashMap<>();
            result.put("email", email);
            result.put("verificationSent", sent);
            
            if (sent) {
                return ResponseEntity.ok(ApiResponse.success(
                    "Verification email sent. Please check your inbox and click the verification link.", 
                    result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Failed to send verification email. Please try again later.", 
                    result));
            }
        } catch (Exception e) {
            logger.error("Failed to verify email: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to verify email: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-email-verification")
    @Operation(summary = "Check if an email address is verified with AWS SES (only works when using aws-ses provider)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEmailVerification(@RequestParam String email) {
        if (!isAwsSes) {
            Map<String, Object> result = new HashMap<>();
            result.put("email", email);
            result.put("provider", "standard");
            return ResponseEntity.ok(ApiResponse.success("Not using AWS SES provider, verification not required", result));
        }
        
        try {
            AwsSesEmailService sesService = (AwsSesEmailService) emailService;
            boolean isVerified = sesService.isEmailVerified(email);
            
            Map<String, Object> result = new HashMap<>();
            result.put("email", email);
            result.put("verified", isVerified);
            
            return ResponseEntity.ok(ApiResponse.success(
                isVerified ? "Email is verified" : "Email is not verified", 
                result));
        } catch (Exception e) {
            logger.error("Failed to check email verification: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to check email verification: " + e.getMessage()));
        }
    }
}

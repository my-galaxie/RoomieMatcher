package com.roomiematcher.notification.controller;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import com.roomiematcher.notification.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Management", description = "APIs for sending email notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final EmailService emailService;
    
    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
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
}

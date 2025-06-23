package com.roomiematcher.notification.controller;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import com.roomiematcher.notification.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "APIs for sending email notifications")
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    @Operation(summary = "Send an email notification")
    public ResponseEntity<ApiResponse<String>> sendEmailNotification(
            @Valid @RequestBody EmailRequestDTO emailRequest) {
        log.debug("Received email request to: {}", emailRequest.getTo());
        
        try {
            emailService.sendEmail(emailRequest);
            return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to send email: " + e.getMessage()));
        }
    }
} 
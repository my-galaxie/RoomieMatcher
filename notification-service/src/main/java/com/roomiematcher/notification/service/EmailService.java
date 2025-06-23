package com.roomiematcher.notification.service;

import com.roomiematcher.common.dto.notification.EmailRequestDTO;

/**
 * Interface for email sending services
 */
public interface EmailService {
    
    /**
     * Sends an email based on the provided request
     * @param request the email request containing recipient, subject, content, etc.
     */
    void sendEmail(EmailRequestDTO request);
} 
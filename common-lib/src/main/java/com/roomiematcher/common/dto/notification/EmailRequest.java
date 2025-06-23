package com.roomiematcher.common.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    
    @NotBlank(message = "To address is required")
    @Email(message = "Please provide a valid email address")
    private String to;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    private String body;
    
    private String template;
    
    private Map<String, Object> templateVariables;
} 
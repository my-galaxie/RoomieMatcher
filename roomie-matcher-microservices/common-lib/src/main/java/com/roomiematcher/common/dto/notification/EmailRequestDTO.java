package com.roomiematcher.common.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    @NotBlank
    @Email
    private String to;

    @NotBlank
    private String subject;

    private String text;

    private String template;

    @Builder.Default
    private Map<String, Object> templateVariables = new HashMap<>();

    private boolean isHtml;
} 
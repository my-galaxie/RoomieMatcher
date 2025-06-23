package com.roomiematcher.common.dto.notification;

import java.util.Map;

public class EmailRequestDTO {
    private String to;
    private String subject;
    private String body;
    private String template;
    private Map<String, Object> templateVariables;
    private EmailType type;
    
    public EmailRequestDTO() {
    }
    
    public EmailRequestDTO(String to, String subject, String body, String template, Map<String, Object> templateVariables, EmailType type) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.template = template;
        this.templateVariables = templateVariables;
        this.type = type;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getTemplate() {
        return template;
    }
    
    public void setTemplate(String template) {
        this.template = template;
    }
    
    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }
    
    public void setTemplateVariables(Map<String, Object> templateVariables) {
        this.templateVariables = templateVariables;
    }
    
    public EmailType getType() {
        return type;
    }
    
    public void setType(EmailType type) {
        this.type = type;
    }
    
    public enum EmailType {
        SIMPLE,
        VERIFICATION,
        PASSWORD_RESET,
        MATCH_NOTIFICATION
    }
} 
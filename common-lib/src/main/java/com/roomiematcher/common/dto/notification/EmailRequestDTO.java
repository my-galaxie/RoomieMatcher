package com.roomiematcher.common.dto.notification;

import java.util.Map;
import java.util.List;
import java.util.Collections;

public class EmailRequestDTO {
    private List<String> to;
    private String subject;
    private String body;
    private String template;
    private Map<String, Object> templateVariables;
    private EmailType type;
    private boolean isHtml;
    
    public EmailRequestDTO() {
    }
    
    public EmailRequestDTO(List<String> to, String subject, String body, String template, Map<String, Object> templateVariables, EmailType type, boolean isHtml) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.template = template;
        this.templateVariables = templateVariables;
        this.type = type;
        this.isHtml = isHtml;
    }
    
    // Convenience constructor for single recipient
    public EmailRequestDTO(String to, String subject, String body, String template, Map<String, Object> templateVariables, EmailType type, boolean isHtml) {
        this.to = Collections.singletonList(to);
        this.subject = subject;
        this.body = body;
        this.template = template;
        this.templateVariables = templateVariables;
        this.type = type;
        this.isHtml = isHtml;
    }
    
    public List<String> getTo() {
        return to;
    }
    
    public void setTo(List<String> to) {
        this.to = to;
    }
    
    // Convenience method for setting a single recipient
    public void setTo(String to) {
        this.to = Collections.singletonList(to);
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
    
    public boolean isHtml() {
        return isHtml;
    }
    
    public void setHtml(boolean isHtml) {
        this.isHtml = isHtml;
    }
    
    public enum EmailType {
        SIMPLE,
        VERIFICATION,
        PASSWORD_RESET,
        MATCH_NOTIFICATION
    }
} 
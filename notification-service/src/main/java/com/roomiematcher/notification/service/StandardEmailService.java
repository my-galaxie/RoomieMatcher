package com.roomiematcher.notification.service;

import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "notification.provider", havingValue = "standard", matchIfMissing = true)
public class StandardEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(StandardEmailService.class);
    
    private final TemplateEngine templateEngine;
    
    public StandardEmailService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(EmailRequestDTO request) {
        String content = request.getBody();
        
        if (request.getTemplate() != null && !request.getTemplate().isEmpty()) {
            Context context = new Context(Locale.getDefault());
            if (request.getTemplateVariables() != null) {
                request.getTemplateVariables().forEach(context::setVariable);
            }
            content = templateEngine.process(request.getTemplate(), context);
        }
        
        // In free tier deployment, we just log the emails instead of sending them
        logger.info("FREE TIER EMAIL NOTIFICATION");
        
        // Handle multiple recipients
        String recipients = request.getTo() != null ? 
            request.getTo().stream().collect(Collectors.joining(", ")) : 
            "No recipients";
            
        logger.info("To: {}", recipients);
        logger.info("Subject: {}", request.getSubject());
        logger.info("Content: {}", content);
    }
}

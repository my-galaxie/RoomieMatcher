package com.roomiematcher.notification.service;

import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@ConditionalOnProperty(name = "notification.provider", havingValue = "standard", matchIfMissing = true)
public class StandardEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(StandardEmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public StandardEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(EmailRequestDTO request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            
            String content = request.getBody();
            
            // Process template if specified
            if (request.getTemplate() != null && !request.getTemplate().isEmpty()) {
                Context context = new Context(Locale.getDefault());
                if (request.getTemplateVariables() != null) {
                    request.getTemplateVariables().forEach(context::setVariable);
                }
                content = templateEngine.process(request.getTemplate(), context);
            }
            
            helper.setText(content, request.isHtml());
            
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", request.getTo());
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

package com.roomiematcher.notification.service;

import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notification.provider", havingValue = "standard", matchIfMissing = true)
public class StandardEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(EmailRequestDTO request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            
            String content = request.getText();
            
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
            log.info("Email sent successfully to: {}", request.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 
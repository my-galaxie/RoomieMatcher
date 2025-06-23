package com.roomiematcher.notification.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@Slf4j
@ConditionalOnProperty(name = "notification.provider", havingValue = "aws-ses")
public class AwsSesEmailService implements EmailService {

    private final AmazonSimpleEmailService sesClient;
    private final String fromEmail;
    private final TemplateEngine templateEngine;

    public AwsSesEmailService(
            @Value("${aws.ses.access-key}") String accessKey,
            @Value("${aws.ses.secret-key}") String secretKey,
            @Value("${aws.ses.region}") String region,
            @Value("${aws.ses.from-email}") String fromEmail,
            TemplateEngine templateEngine
    ) {
        this.fromEmail = fromEmail;
        this.templateEngine = templateEngine;

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }

    @Override
    public void sendEmail(EmailRequestDTO request) {
        try {
            String content = request.getBody();
            
            if (request.getTemplate() != null && !request.getTemplate().isEmpty()) {
                Context context = new Context(Locale.getDefault());
                if (request.getTemplateVariables() != null) {
                    request.getTemplateVariables().forEach(context::setVariable);
                }
                content = templateEngine.process(request.getTemplate(), context);
            }

            SendEmailRequest sendRequest = new SendEmailRequest()
                    .withSource(fromEmail)
                    .withDestination(
                            new Destination().withToAddresses(request.getTo())
                    )
                    .withMessage(
                            new Message()
                                    .withSubject(new Content().withCharset("UTF-8").withData(request.getSubject()))
                                    .withBody(new Body().withHtml(
                                            new Content().withCharset("UTF-8").withData(content)
                                    ))
                    );

            sesClient.sendEmail(sendRequest);
            log.info("Email sent successfully to: {}", request.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 
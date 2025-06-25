package com.roomiematcher.notification.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import com.roomiematcher.common.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.annotation.PostConstruct;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

@Service
@ConditionalOnProperty(name = "notification.provider", havingValue = "aws-ses")
public class AwsSesEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(AwsSesEmailService.class);

    private final AmazonSimpleEmailService sesClient;
    private final String fromEmail;
    private final TemplateEngine templateEngine;
    private final String awsRegion;
    private boolean isSandboxMode = true;
    private final Set<String> verifiedEmails = new HashSet<>();

    public AwsSesEmailService(
            @Value("${aws.ses.access-key:#{null}}") String accessKey,
            @Value("${aws.ses.secret-key:#{null}}") String secretKey,
            @Value("${aws.ses.region}") String region,
            @Value("${aws.ses.from-email}") String fromEmail,
            TemplateEngine templateEngine
    ) {
        this.fromEmail = fromEmail;
        this.templateEngine = templateEngine;
        this.awsRegion = region;

        logger.info("Initializing AWS SES client in region: {}", region);
        
        // Use either provided credentials or instance profile credentials
        if (accessKey != null && !accessKey.isEmpty() && secretKey != null && !secretKey.isEmpty()) {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            this.sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(region)
                    .build();
            logger.info("AWS SES client initialized with provided credentials");
        } else {
            this.sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .withRegion(region)
                    .build();
            logger.info("AWS SES client initialized with instance profile credentials");
        }
    }

    @PostConstruct
    public void checkSesSetup() {
        try {
            // Check if email is verified
            GetIdentityVerificationAttributesRequest request = 
                new GetIdentityVerificationAttributesRequest().withIdentities(fromEmail);
            GetIdentityVerificationAttributesResult result = 
                sesClient.getIdentityVerificationAttributes(request);
            
            if (result.getVerificationAttributes().containsKey(fromEmail)) {
                String status = result.getVerificationAttributes().get(fromEmail).getVerificationStatus();
                logger.info("SES Email verification status for {}: {}", fromEmail, status);
                
                if (!"Success".equals(status)) {
                    logger.warn("SES email {} is not verified! Emails may not be sent.", fromEmail);
                } else {
                    verifiedEmails.add(fromEmail);
                }
            } else {
                logger.warn("SES email {} is not registered with SES. Attempting to verify...", fromEmail);
                sesClient.verifyEmailIdentity(new VerifyEmailIdentityRequest().withEmailAddress(fromEmail));
                logger.info("Verification email sent to {}. Please check inbox and verify.", fromEmail);
            }
            
            // Check sending limits and sandbox status
            try {
                boolean sendingEnabled = sesClient.getAccountSendingEnabled(new GetAccountSendingEnabledRequest()).getEnabled();
                logger.info("SES Account Sending Enabled: {}", sendingEnabled);
            } catch (Exception e) {
                logger.warn("Could not determine if account sending is enabled: {}", e.getMessage());
            }
            
            GetSendQuotaResult quotaResult = sesClient.getSendQuota();
            logger.info("SES Daily Sending Quota: {}", quotaResult.getMax24HourSend());
            logger.info("SES Sending Rate: {} emails/second", quotaResult.getMaxSendRate());
            logger.info("SES Sent Last 24 Hours: {}", quotaResult.getSentLast24Hours());
            
            // Check if in sandbox mode (quota less than 200 indicates sandbox)
            if (quotaResult.getMax24HourSend() < 200) {
                logger.warn("AWS SES account is in SANDBOX mode. Only verified email addresses can receive emails.");
                isSandboxMode = true;
                
                // Load all verified identities
                ListIdentitiesResult identities = sesClient.listIdentities(
                    new ListIdentitiesRequest().withIdentityType(IdentityType.EmailAddress));
                
                if (!identities.getIdentities().isEmpty()) {
                    GetIdentityVerificationAttributesResult verificationResults = 
                        sesClient.getIdentityVerificationAttributes(
                            new GetIdentityVerificationAttributesRequest()
                                .withIdentities(identities.getIdentities()));
                    
                    for (String identity : identities.getIdentities()) {
                        IdentityVerificationAttributes attributes = 
                            verificationResults.getVerificationAttributes().get(identity);
                        if (attributes != null && "Success".equals(attributes.getVerificationStatus())) {
                            verifiedEmails.add(identity);
                            logger.info("Added verified email: {}", identity);
                        }
                    }
                }
                
                logger.info("Total verified emails: {}", verifiedEmails.size());
            } else {
                isSandboxMode = false;
                logger.info("AWS SES account is in PRODUCTION mode.");
            }
            
        } catch (Exception e) {
            logger.error("Error checking SES setup: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendEmail(EmailRequestDTO request) {
        try {
            // In sandbox mode, check if recipient is verified
            if (isSandboxMode && request.getTo() != null) {
                for (String recipient : request.getTo()) {
                    if (!verifiedEmails.contains(recipient)) {
                        logger.warn("Cannot send email to unverified recipient {} in sandbox mode", recipient);
                        try {
                            sesClient.verifyEmailIdentity(
                                new VerifyEmailIdentityRequest().withEmailAddress(recipient));
                            logger.info("Verification email sent to {}. User needs to verify before receiving emails.", 
                                recipient);
                        } catch (Exception e) {
                            logger.error("Failed to initiate email verification for {}: {}", 
                                recipient, e.getMessage());
                        }
                        throw new BadRequestException("Recipient email " + recipient + 
                            " is not verified. A verification email has been sent. " +
                            "Please check the inbox and verify before trying again.");
                    }
                }
            }
            
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

            SendEmailResult result = sesClient.sendEmail(sendRequest);
            logger.info("Email sent successfully to: {}. Message ID: {}", request.getTo(), result.getMessageId());
        } catch (MessageRejectedException e) {
            logger.error("Email rejected by SES: {} - To: {}", e.getMessage(), request.getTo());
            if (e.getMessage().contains("Email address is not verified")) {
                logger.info("Attempting to verify recipient email: {}", request.getTo());
                try {
                    if (request.getTo() != null) {
                        for (String recipient : request.getTo()) {
                            sesClient.verifyEmailIdentity(new VerifyEmailIdentityRequest().withEmailAddress(recipient));
                            logger.info("Verification email sent to {}. User needs to verify before receiving emails.", 
                                recipient);
                        }
                    }
                } catch (Exception verifyEx) {
                    logger.error("Failed to initiate email verification: {}", verifyEx.getMessage());
                }
                throw new BadRequestException("Email address not verified. A verification email has been sent. " +
                    "Please check your inbox and verify before trying again.");
            }
            throw new BadRequestException("Failed to send email: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", request.getTo(), e.getMessage(), e);
            throw new BadRequestException("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Manually verify an email address with SES
     * @param email The email address to verify
     * @return true if verification request was sent successfully
     */
    public boolean verifyEmailAddress(String email) {
        try {
            sesClient.verifyEmailIdentity(new VerifyEmailIdentityRequest().withEmailAddress(email));
            logger.info("Verification email sent to {}", email);
            return true;
        } catch (Exception e) {
            logger.error("Failed to initiate verification for {}: {}", email, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if an email is verified with SES
     * @param email The email address to check
     * @return true if the email is verified
     */
    public boolean isEmailVerified(String email) {
        try {
            GetIdentityVerificationAttributesRequest request = 
                new GetIdentityVerificationAttributesRequest().withIdentities(email);
            GetIdentityVerificationAttributesResult result = 
                sesClient.getIdentityVerificationAttributes(request);
            
            if (result.getVerificationAttributes().containsKey(email)) {
                String status = result.getVerificationAttributes().get(email).getVerificationStatus();
                return "Success".equals(status);
            }
            return false;
        } catch (Exception e) {
            logger.error("Failed to check verification status for {}: {}", email, e.getMessage());
            return false;
        }
    }
}

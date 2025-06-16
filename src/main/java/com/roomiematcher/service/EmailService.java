package com.roomiematcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Sends a simple email message
     *
     * @param to the recipient email address
     * @param subject the email subject
     * @param body the email body
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            // Try to send the email, but don't break if it fails
            try {
                mailSender.send(message);
                System.out.println("Email sent successfully to: " + to);
            } catch (Exception e) {
                // Log the exception but don't throw it to avoid breaking the application flow
                System.err.println("Failed to send email: " + e.getMessage());
                
                // Print the email content to the console for development purposes
                System.out.println("==================================================");
                System.out.println("DEVELOPMENT MODE: Email would have been sent");
                System.out.println("To: " + to);
                System.out.println("Subject: " + subject);
                System.out.println("Body: " + body);
                System.out.println("==================================================");
            }
        } catch (Exception e) {
            // Log any other exceptions
            System.err.println("Error preparing email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends an OTP verification email to the user
     *
     * @param to the recipient email address
     * @param otp the one-time password
     */
    public void sendOtpEmail(String to, String otp) {
        String subject = "RoomieMatcher - Email Verification Code";
        String body = "Hello,\n\n"
                + "Thank you for registering with RoomieMatcher. Your verification code is: " + otp + "\n\n"
                + "This code will expire in 10 minutes.\n\n"
                + "If you did not request this code, please ignore this email.\n\n"
                + "Best regards,\n"
                + "The RoomieMatcher Team";
        
        sendSimpleEmail(to, subject, body);
    }
    
    /**
     * Sends a password reset OTP email to the user
     *
     * @param to the recipient email address
     * @param otp the one-time password for password reset
     */
    public void sendPasswordResetEmail(String to, String otp) {
        String subject = "RoomieMatcher - Password Reset Code";
        String body = "Hello,\n\n"
                + "We received a request to reset your password for your RoomieMatcher account. Your password reset code is: " + otp + "\n\n"
                + "This code will expire in 10 minutes.\n\n"
                + "If you did not request a password reset, please ignore this email or contact support if you have concerns.\n\n"
                + "Best regards,\n"
                + "The RoomieMatcher Team";
        
        sendSimpleEmail(to, subject, body);
    }
} 
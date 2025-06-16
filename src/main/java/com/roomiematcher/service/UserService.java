package com.roomiematcher.service;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.model.User;
import com.roomiematcher.repository.TenantRepository;
import com.roomiematcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Value("${app.otp.expiration-minutes:10}")
    private int otpExpirationMinutes;

    /**
     * Registers a new user by encrypting their password and saving them to the database.
     * Validates that the email is not already in use.
     *
     * @param user the user to register
     * @return the saved user entity or null if registration fails
     * @throws IllegalArgumentException if the email is already in use
     */
    @Transactional
    public User registerUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use: " + user.getEmail());
        }
        
        // Encrypt the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Generate and set OTP for email verification
        String otp = generateOtp();
        user.setVerificationCode(otp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        user.setIsActive(false);
        
        // Save the user to the database
        User savedUser = userRepository.save(user);
        
        try {
            // Send verification email
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // For development purposes, print the OTP to the console
            System.out.println("==================================================");
            System.out.println("DEVELOPMENT MODE: OTP for " + user.getEmail() + " is: " + otp);
            System.out.println("==================================================");
        }
        
        return savedUser;
    }
    
    /**
     * Generates a random 6-digit OTP
     * 
     * @return the generated OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit number
        return String.valueOf(otp);
    }
    
    /**
     * Verifies the OTP entered by the user
     * 
     * @param email the user's email
     * @param otp the OTP entered by the user
     * @return true if verification is successful, false otherwise
     */
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return false;
        }
        
        if (user.getVerificationCode() == null || user.getVerificationExpiry() == null) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(user.getVerificationExpiry())) {
            return false; // OTP expired
        }
        
        if (user.getVerificationCode().equals(otp)) {
            user.setIsActive(true);
            user.setVerificationCode(null);
            user.setVerificationExpiry(null);
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
    
    /**
     * Resends the OTP to the user's email
     * 
     * @param email the user's email
     * @return true if OTP was resent, false otherwise
     */
    @Transactional
    public boolean resendOtp(String email) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return false;
        }
        
        String otp = generateOtp();
        user.setVerificationCode(otp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userRepository.save(user);
        
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // For development purposes, print the OTP to the console
            System.out.println("==================================================");
            System.out.println("DEVELOPMENT MODE: OTP for " + user.getEmail() + " is: " + otp);
            System.out.println("==================================================");
        }
        
        return true;
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return the user entity if found, otherwise null
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Gets the currently authenticated user.
     *
     * @return the authenticated user entity if found, otherwise null
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        return userRepository.findByEmail(auth.getName());
    }
    
    /**
     * Updates tenant preferences.
     *
     * @param tenant the tenant with updated preferences
     * @return the updated tenant entity
     */
    @Transactional
    public Tenant updatePreferences(Tenant tenant) {
        return tenantRepository.save(tenant);
    }
    
    /**
     * Checks if an email is already registered.
     * 
     * @param email the email to check
     * @return true if the email is already in use, false otherwise
     */
    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }
    
    /**
     * Initiates the password reset process by generating and sending an OTP
     * 
     * @param email the email of the user requesting password reset
     * @return true if the password reset was initiated, false if the email is not registered
     */
    @Transactional
    public boolean initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return false;
        }
        
        String otp = generateOtp();
        user.setVerificationCode(otp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userRepository.save(user);
        
        try {
            emailService.sendPasswordResetEmail(email, otp);
        } catch (Exception e) {
            // For development purposes, print the OTP to the console
            System.out.println("==================================================");
            System.out.println("DEVELOPMENT MODE: Password Reset OTP for " + email + " is: " + otp);
            System.out.println("==================================================");
        }
        
        return true;
    }
    
    /**
     * Resends the password reset OTP
     * 
     * @param email the email of the user
     * @return true if OTP was resent, false otherwise
     */
    @Transactional
    public boolean resendPasswordResetOtp(String email) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return false;
        }
        
        String otp = generateOtp();
        user.setVerificationCode(otp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userRepository.save(user);
        
        try {
            emailService.sendPasswordResetEmail(email, otp);
        } catch (Exception e) {
            // For development purposes, print the OTP to the console
            System.out.println("==================================================");
            System.out.println("DEVELOPMENT MODE: Password Reset OTP for " + email + " is: " + otp);
            System.out.println("==================================================");
        }
        
        return true;
    }
    
    /**
     * Completes the password reset process by verifying the OTP and updating the password
     * 
     * @param email the email of the user
     * @param otp the OTP entered by the user
     * @param newPassword the new password
     * @return true if password was reset successfully, false otherwise
     */
    @Transactional
    public boolean resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return false;
        }
        
        if (user.getVerificationCode() == null || user.getVerificationExpiry() == null) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(user.getVerificationExpiry())) {
            return false; // OTP expired
        }
        
        if (user.getVerificationCode().equals(otp)) {
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);
            user.setVerificationExpiry(null);
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
}

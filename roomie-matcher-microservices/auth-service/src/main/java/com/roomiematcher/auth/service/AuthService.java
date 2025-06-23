package com.roomiematcher.auth.service;

import com.roomiematcher.auth.client.NotificationClient;
import com.roomiematcher.auth.model.OtpVerification;
import com.roomiematcher.auth.model.User;
import com.roomiematcher.auth.repository.OtpVerificationRepository;
import com.roomiematcher.auth.repository.UserRepository;
import com.roomiematcher.auth.security.JwtTokenProvider;
import com.roomiematcher.common.dto.auth.AuthRequest;
import com.roomiematcher.common.dto.auth.AuthResponse;
import com.roomiematcher.common.dto.auth.UserDTO;
import com.roomiematcher.common.dto.notification.EmailRequestDTO;
import com.roomiematcher.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final NotificationClient notificationClient;
    
    /**
     * Register a new user
     */
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        // Create new user
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode("password")); // Default password
        user.setIsActive(false);
        
        // Generate verification code
        String verificationCode = generateOtp();
        user.setVerificationCode(verificationCode);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(10));
        
        User savedUser = userRepository.save(user);
        
        // Send notification (simplified)
        sendVerificationEmail(user.getEmail(), verificationCode);
        
        return convertToDTO(savedUser);
    }
    
    /**
     * Authenticate a user
     */
    public AuthResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authRequest.getEmail()));
        
        String token = tokenProvider.generateToken(authentication);
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isVerified(user.getIsActive())
                .build();
    }
    
    /**
     * Verify OTP code
     */
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        if (user.getVerificationCode() != null && 
            user.getVerificationCode().equals(otp) &&
            user.getVerificationExpiry() != null && 
            LocalDateTime.now().isBefore(user.getVerificationExpiry())) {
            
            user.setIsActive(true);
            user.setVerificationCode(null);
            user.setVerificationExpiry(null);
            userRepository.save(user);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Resend OTP for verification
     */
    @Transactional
    public boolean resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        // Generate new verification code
        String newOtp = generateOtp();
        user.setVerificationCode(newOtp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        // Send new verification email
        try {
            sendVerificationEmail(email, newOtp);
            return true;
        } catch (Exception e) {
            log.error("Failed to resend verification email: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper methods
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    private void sendVerificationEmail(String email, String code) {
        try {
            EmailRequestDTO emailRequest = EmailRequestDTO.builder()
                    .to(email)
                    .subject("Email Verification")
                    .body("Your verification code is: " + code)
                    .type("VERIFICATION")
                    .build();
                    
            notificationClient.sendVerificationEmail(emailRequest);
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage());
        }
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .gender(user.getGender())
                .active(user.getIsActive())
                .build();
    }
} 
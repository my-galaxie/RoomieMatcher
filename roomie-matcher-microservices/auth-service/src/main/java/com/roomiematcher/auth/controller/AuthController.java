package com.roomiematcher.auth.controller;

import com.roomiematcher.auth.service.AuthService;
import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.auth.AuthRequest;
import com.roomiematcher.common.dto.auth.AuthResponse;
import com.roomiematcher.common.dto.auth.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = authService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully. Please verify your email.", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@Valid @RequestBody AuthRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        boolean verified = authService.verifyOtp(email, otp);
        
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid or expired verification code"));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Boolean>> resendOtp(@RequestParam String email) {
        boolean sent = authService.resendOtp(email);
        
        if (sent) {
            return ResponseEntity.ok(ApiResponse.success("Verification code resent successfully", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to resend verification code"));
        }
    }
} 
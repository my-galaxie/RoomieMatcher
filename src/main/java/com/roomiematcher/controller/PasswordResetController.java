package com.roomiematcher.controller;

import com.roomiematcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;

    /**
     * Shows the forgot password page
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    /**
     * Processes the forgot password request
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, 
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        
        // Validate email format
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Please enter a valid email address");
            return "forgot-password";
        }
        
        // Check if email exists
        if (!userService.isEmailRegistered(email)) {
            model.addAttribute("error", "No account found with this email address");
            return "forgot-password";
        }
        
        // Initiate password reset
        boolean initiated = userService.initiatePasswordReset(email);
        
        if (initiated) {
            redirectAttributes.addFlashAttribute("message", "A password reset code has been sent to your email");
            return "redirect:/reset-password?email=" + email;
        } else {
            model.addAttribute("error", "Failed to send password reset code");
            return "forgot-password";
        }
    }

    /**
     * Shows the reset password page
     */
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "reset-password";
    }

    /**
     * Processes the password reset
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String email,
                                     @RequestParam String otp,
                                     @RequestParam String newPassword,
                                     @RequestParam String confirmPassword,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        
        // Validate email format
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Invalid email address");
            model.addAttribute("email", email);
            return "reset-password";
        }
        
        // Validate OTP format
        if (!otp.matches("^\\d{6}$")) {
            model.addAttribute("error", "Please enter a valid 6-digit verification code");
            model.addAttribute("email", email);
            return "reset-password";
        }
        
        // Validate password
        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long");
            model.addAttribute("email", email);
            return "reset-password";
        }
        
        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("email", email);
            return "reset-password";
        }
        
        // Reset password
        boolean reset = userService.resetPassword(email, otp, newPassword);
        
        if (reset) {
            redirectAttributes.addFlashAttribute("passwordReset", true);
            return "redirect:/login?passwordReset=true";
        } else {
            model.addAttribute("error", "Invalid or expired verification code");
            model.addAttribute("email", email);
            return "reset-password";
        }
    }

    /**
     * Resends the password reset OTP
     */
    @PostMapping("/resend-reset-otp")
    public String resendResetOtp(@RequestParam String email, RedirectAttributes redirectAttributes) {
        boolean sent = userService.resendPasswordResetOtp(email);
        
        if (sent) {
            redirectAttributes.addFlashAttribute("message", "A new verification code has been sent to your email");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to resend verification code");
        }
        
        redirectAttributes.addAttribute("email", email);
        return "redirect:/reset-password";
    }
} 
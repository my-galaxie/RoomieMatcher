package com.roomiematcher.controller;

import com.roomiematcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VerificationController {

    @Autowired
    private UserService userService;

    @GetMapping("/verify-otp")
    public String showVerificationPage(@RequestParam(required = false) String email, 
                                      Model model,
                                      @RequestParam(required = false) String message,
                                      @RequestParam(required = false) String error) {
        if (email != null && !email.isEmpty()) {
            model.addAttribute("email", email);
        }
        
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }
        
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }
        
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, 
                           @RequestParam String otp,
                           RedirectAttributes redirectAttributes) {
        
        boolean verified = userService.verifyOtp(email, otp);
        
        if (verified) {
            redirectAttributes.addFlashAttribute("verificationSuccess", true);
            return "redirect:/login?verified=true";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired verification code");
            redirectAttributes.addAttribute("email", email);
            return "redirect:/verify-otp";
        }
    }
    
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, RedirectAttributes redirectAttributes) {
        boolean sent = userService.resendOtp(email);
        
        if (sent) {
            redirectAttributes.addFlashAttribute("message", "A new verification code has been sent to your email");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to resend verification code");
        }
        
        redirectAttributes.addAttribute("email", email);
        return "redirect:/verify-otp";
    }
} 
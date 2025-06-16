package com.roomiematcher.controller;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.model.User;
import com.roomiematcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("tenant", new Tenant());
        return "register";
    }

    // Process registration
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("tenant") Tenant tenant, 
                                     BindingResult result, 
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        // Basic validation
        if (tenant.getName() == null || tenant.getName().trim().isEmpty()) {
            result.rejectValue("name", "error.name", "Name is required");
        }
        
        if (tenant.getEmail() == null || tenant.getEmail().trim().isEmpty()) {
            result.rejectValue("email", "error.email", "Email is required");
        } else if (!isValidEmail(tenant.getEmail())) {
            result.rejectValue("email", "error.email", "Please enter a valid email address");
        }
        
        if (tenant.getPassword() == null || tenant.getPassword().trim().isEmpty()) {
            result.rejectValue("password", "error.password", "Password is required");
        } else if (tenant.getPassword().length() < 6) {
            result.rejectValue("password", "error.password", "Password must be at least 6 characters long");
        }
        
        // Check if email is already registered
        if (tenant.getEmail() != null && !tenant.getEmail().trim().isEmpty() 
                && isValidEmail(tenant.getEmail())
                && userService.isEmailRegistered(tenant.getEmail())) {
            result.rejectValue("email", "error.email", "An account with this email already exists. Please login instead.");
            model.addAttribute("accountExists", true);
            model.addAttribute("existingEmail", tenant.getEmail());
        }
        
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            User registeredUser = userService.registerUser(tenant);
            redirectAttributes.addFlashAttribute("registrationSuccess", true);
            redirectAttributes.addFlashAttribute("message", "Please check your email for a verification code");
            return "redirect:/verify-otp?email=" + tenant.getEmail();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    // Show login form (Spring Security will handle authentication)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    /**
     * Validates email format using a regular expression
     * 
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && email.matches(emailRegex);
    }
}

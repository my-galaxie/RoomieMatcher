package com.roomiematcher.controller;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.model.User;
import com.roomiematcher.repository.TestimonialRepository;
import com.roomiematcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private TestimonialRepository testimonialRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        try {
            // Debug: Print authentication information
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + auth);
            if (auth != null) {
                System.out.println("Auth name: " + auth.getName());
                System.out.println("Auth principal: " + auth.getPrincipal());
                System.out.println("Auth authorities: " + auth.getAuthorities());
            }
            
            // Get the current logged-in user
            User currentUser = userService.getCurrentUser();
            System.out.println("Current user: " + currentUser);
            
            if (currentUser == null) {
                // If not logged in, redirect to login page
                System.out.println("No current user found, redirecting to login");
                return "redirect:/login";
            }
            
            model.addAttribute("user", currentUser);
            
            // Check if the user has set preferences
            boolean hasSetPreferences = false;
            String location = null;
            Double budget = null;
            
            try {
                if (currentUser instanceof Tenant) {
                    Tenant tenant = (Tenant) currentUser;
                    System.out.println("User is a tenant: " + tenant.getId() + 
                                      ", location: " + tenant.getLocation() + 
                                      ", budget: " + tenant.getBudget());
                    
                    location = tenant.getLocation();
                    budget = tenant.getBudget();
                    
                    hasSetPreferences = location != null && 
                                        !location.isEmpty() && 
                                        budget != null;
                } else {
                    System.out.println("User is not a tenant: " + currentUser.getClass().getName());
                }
            } catch (Exception e) {
                System.err.println("Error checking tenant preferences: " + e.getMessage());
                // Continue with default value of hasSetPreferences = false
            }
            
            model.addAttribute("hasSetPreferences", hasSetPreferences);
            model.addAttribute("location", location);
            model.addAttribute("budget", budget);
            
            return "dashboard";
        } catch (Exception e) {
            System.err.println("Error in dashboard controller: " + e.getMessage());
            e.printStackTrace();
            return "error"; // Return the error page instead of rethrowing
        }
    }
    
    @GetMapping("/edit-profile")
    public String editProfile() {
        // Redirect to preferences page which already has the user's profile information
        return "redirect:/preferences";
    }
} 
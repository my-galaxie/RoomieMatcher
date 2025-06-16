package com.roomiematcher.controller;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.model.Testimonial;
import com.roomiematcher.repository.TenantRepository;
import com.roomiematcher.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private TestimonialRepository testimonialRepository;

    @GetMapping("/")
    public String showHomePage(Model model) {
        try {
            // Get testimonials for display
            List<Testimonial> testimonials = testimonialRepository.findAllByOrderByDisplayOrderAsc();
            model.addAttribute("testimonials", testimonials);
            
            return "home";
        } catch (Exception e) {
            // Log the error
            System.err.println("Error loading home page: " + e.getMessage());
            e.printStackTrace();
            
            // Add error message to model
            model.addAttribute("error", "An error occurred while loading the home page.");
            return "error";
        }
    }
    
    @GetMapping("/about")
    public String showAboutPage() {
        return "redirect:/#about";
    }
    
    @GetMapping("/contact")
    public String showContactPage() {
        return "redirect:/#contact";
    }
}

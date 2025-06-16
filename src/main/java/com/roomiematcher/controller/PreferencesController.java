package com.roomiematcher.controller;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.model.User;
import com.roomiematcher.repository.TenantRepository;
import com.roomiematcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/preferences")
public class PreferencesController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private TenantRepository tenantRepository;

    @GetMapping
    public String showPreferencesForm(Model model) {
        User currentUser = userService.getCurrentUser();
        
        // If the user is already a Tenant, use their data
        if (currentUser instanceof Tenant) {
            model.addAttribute("tenant", currentUser);
        } else {
            // Otherwise create a new Tenant with the user's basic info
            Tenant tenant = new Tenant();
            tenant.setName(currentUser.getName());
            tenant.setEmail(currentUser.getEmail());
            tenant.setPassword(currentUser.getPassword());
            model.addAttribute("tenant", tenant);
        }
        
        return "preferences";
    }

    @PostMapping
    public String savePreferences(@ModelAttribute("tenant") Tenant tenant) {
        User currentUser = userService.getCurrentUser();
        
        // If the user already exists as a tenant, fetch it
        Tenant existingTenant = null;
        if (currentUser instanceof Tenant) {
            existingTenant = tenantRepository.findById(currentUser.getId()).orElse(null);
        }
        
        if (existingTenant != null) {
            // Update existing tenant's preferences
            existingTenant.setBudget(tenant.getBudget());
            existingTenant.setLocation(tenant.getLocation());
            existingTenant.setSmoking(tenant.getSmoking());
            existingTenant.setPets(tenant.getPets());
            existingTenant.setCleanlinessLevel(tenant.getCleanlinessLevel());
            existingTenant.setNoiseTolerance(tenant.getNoiseTolerance());
            existingTenant.setGender(tenant.getGender());
            
            // Update preferred genders
            existingTenant.getPreferredGenders().clear();
            if (tenant.getPreferredGenders() != null) {
                existingTenant.getPreferredGenders().addAll(tenant.getPreferredGenders());
            }
            
            tenantRepository.save(existingTenant);
        } else {
            // Create a new tenant with the current user's info
            tenant.setName(currentUser.getName());
            tenant.setEmail(currentUser.getEmail());
            tenant.setPassword(currentUser.getPassword());
            tenantRepository.save(tenant);
        }
        
        // Redirect to the matches page
        return "redirect:/matches";
    }
} 
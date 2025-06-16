package com.roomiematcher.controller;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.model.User;
import com.roomiematcher.repository.TenantRepository;
import com.roomiematcher.service.RoommateService;
import com.roomiematcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/matches")
public class MatchesController {

    @Autowired
    private UserService userService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoommateService roommateService;

    @GetMapping
    public String showMatches(
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) Boolean smoking,
            @RequestParam(required = false) Boolean pets,
            @RequestParam(required = false) Integer minCleanlinessLevel,
            @RequestParam(required = false) Integer maxCleanlinessLevel,
            @RequestParam(required = false) Integer minNoiseTolerance,
            @RequestParam(required = false) Integer maxNoiseTolerance,
            @RequestParam(required = false) String gender,
            Model model) {
        User currentUser = userService.getCurrentUser();
        
        if (!(currentUser instanceof Tenant)) {
            return "redirect:/preferences";
        }
        
        Tenant currentTenant = (Tenant) currentUser;
        List<Tenant> allTenants = tenantRepository.findAll();
        List<Map<String, Object>> matchData = new ArrayList<>();
        
        for (Tenant tenant : allTenants) {
            if (tenant.getId().equals(currentTenant.getId())) {
                continue;
            }
            
            double score = roommateService.computeCompatibility(currentTenant.getId(), tenant.getId());
            
            // Skip tenants with zero score (location or gender mismatch)
            if (score == 0) {
                continue;
            }
            
            Map<String, Object> match = new HashMap<>();
            match.put("id", tenant.getId());
            match.put("name", tenant.getName());
            match.put("email", tenant.getEmail());
            match.put("budget", tenant.getBudget());
            match.put("location", tenant.getLocation());
            match.put("cleanliness", tenant.getCleanlinessLevel());
            match.put("noise", tenant.getNoiseTolerance());
            match.put("smoking", tenant.getSmoking());
            match.put("pets", tenant.getPets());
            match.put("gender", tenant.getGender());
            match.put("score", score);
            
            matchData.add(match);
        }
        
        // Apply filters if provided
        if (minBudget != null) {
            matchData = matchData.stream()
                .filter(m -> (Double) m.get("budget") >= minBudget)
                .collect(Collectors.toList());
        }
        
        if (maxBudget != null) {
            matchData = matchData.stream()
                .filter(m -> (Double) m.get("budget") <= maxBudget)
                .collect(Collectors.toList());
        }
        
        if (smoking != null) {
            matchData = matchData.stream()
                .filter(m -> m.get("smoking").equals(smoking))
                .collect(Collectors.toList());
        }
        
        if (pets != null) {
            matchData = matchData.stream()
                .filter(m -> m.get("pets").equals(pets))
                .collect(Collectors.toList());
        }
        
        if (minCleanlinessLevel != null) {
            matchData = matchData.stream()
                .filter(m -> (Integer) m.get("cleanliness") >= minCleanlinessLevel)
                .collect(Collectors.toList());
        }
        
        if (maxCleanlinessLevel != null) {
            matchData = matchData.stream()
                .filter(m -> (Integer) m.get("cleanliness") <= maxCleanlinessLevel)
                .collect(Collectors.toList());
        }
        
        if (minNoiseTolerance != null) {
            matchData = matchData.stream()
                .filter(m -> (Integer) m.get("noise") >= minNoiseTolerance)
                .collect(Collectors.toList());
        }
        
        if (maxNoiseTolerance != null) {
            matchData = matchData.stream()
                .filter(m -> (Integer) m.get("noise") <= maxNoiseTolerance)
                .collect(Collectors.toList());
        }
        
        if (gender != null && !gender.isEmpty()) {
            matchData = matchData.stream()
                .filter(m -> m.get("gender").equals(gender))
                .collect(Collectors.toList());
        }
        
        // Sort by match score (highest first)
        matchData.sort((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")));
        
        model.addAttribute("currentTenant", currentTenant);
        model.addAttribute("matches", matchData);
        model.addAttribute("minBudget", minBudget);
        model.addAttribute("maxBudget", maxBudget);
        model.addAttribute("smoking", smoking);
        model.addAttribute("pets", pets);
        model.addAttribute("minCleanlinessLevel", minCleanlinessLevel);
        model.addAttribute("maxCleanlinessLevel", maxCleanlinessLevel);
        model.addAttribute("minNoiseTolerance", minNoiseTolerance);
        model.addAttribute("maxNoiseTolerance", maxNoiseTolerance);
        model.addAttribute("gender", gender);
        
        return "matches";
    }
} 
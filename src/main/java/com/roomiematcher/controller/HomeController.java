package com.roomiematcher.controller;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.repository.TenantRepository;
import com.roomiematcher.service.RoommateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
@Controller
public class HomeController {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoommateService roommateService;

    @GetMapping("/")
    public String showHomePage(Model model) {
        // 1. Fetch the main tenant (ID=1)
        Tenant mainTenant = tenantRepository.findById(1L).orElse(null);

        // 2. Fetch all tenants from DB
        List<Tenant> allTenants = tenantRepository.findAll();

        // 3. Prepare a list to store each tenant’s data + score
        List<Map<String, Object>> tenantData = new ArrayList<>();

        // If mainTenant is null, just show all tenants with no scores
        if (mainTenant == null) {
            // Build data without score
            for (Tenant t : allTenants) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", t.getId());
                map.put("name", t.getName());
                map.put("email", t.getEmail());
                map.put("budget", t.getBudget());
                map.put("location", t.getLocation());
                map.put("score", 0.0); // or skip
                tenantData.add(map);
            }
        } else {
            // mainTenant found, compute scores
            for (Tenant t : allTenants) {
                double score = 0.0;
                if (t.getId().equals(mainTenant.getId())) {
                    continue;
                }
                // If you want to skip the main tenant, remove this block
                // but if you want to show main tenant's "score" as well,
                // compute it as mainTenant vs mainTenant (which might be 100, or 0).
                if (!t.getId().equals(mainTenant.getId())) {
                    score = roommateService.computeCompatibility(mainTenant.getId(), t.getId());
                }
                
                Map<String, Object> map = new HashMap<>();
                map.put("id", t.getId());
                map.put("name", t.getName());
                map.put("email", t.getEmail());
                map.put("budget", t.getBudget());
                map.put("location", t.getLocation());
                map.put("score", score);
                tenantData.add(map);
            }
        }

        // 4. Sort by descending score (optional)
        tenantData.sort((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")));

        // 5. Pass both mainTenant and the tenantData list to Thymeleaf
        model.addAttribute("mainTenant", mainTenant);
        model.addAttribute("tenants", tenantData);

        return "home"; // Renders home.html
    }
}

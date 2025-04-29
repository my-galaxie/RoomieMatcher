package com.roomiematcher.controller;

import com.roomiematcher.service.RoommateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MatchViewController {

    @Autowired
    private RoommateService roommateService;

    @GetMapping("/viewMatch")
    public String viewMatch(@RequestParam Long tenant1Id,
                            @RequestParam Long tenant2Id,
                            Model model) {
        double score = roommateService.computeCompatibility(tenant1Id, tenant2Id);
        model.addAttribute("tenant1Id", tenant1Id);
        model.addAttribute("tenant2Id", tenant2Id);
        model.addAttribute("score", score);
        return "match"; // match.html in the templates folder
    }
}

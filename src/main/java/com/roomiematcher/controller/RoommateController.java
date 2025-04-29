package com.roomiematcher.controller;

import com.roomiematcher.service.RoommateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roommates")
public class RoommateController {

    @Autowired
    private RoommateService roommateService;

    // Endpoint using path variables to calculate match score
    @GetMapping("/match/{tenant1Id}/{tenant2Id}")
    public ResponseEntity<Double> getMatchScore(
            @PathVariable Long tenant1Id,
            @PathVariable Long tenant2Id) {

        double score = roommateService.computeCompatibility(tenant1Id, tenant2Id);
        if (score < 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(score);
    }
}

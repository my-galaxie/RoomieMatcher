package com.roomiematcher.service;

import com.roomiematcher.model.Tenant;
import org.springframework.stereotype.Service;

@Service
public class BasicMatching implements MatchingAlgorithm {

    @Override
    public double calculateMatchScore(Tenant t1, Tenant t2) {
        double score = 0;
        // Example criteria: Add 20 points if locations match
        if (t1.getLocation() != null && t2.getLocation() != null &&
            t1.getLocation().equalsIgnoreCase(t2.getLocation())) {
            score += 20;
        }
        // Add 25 points if budgets are similar (difference within 5000)
        if (t1.getBudget() != null && t2.getBudget() != null &&
            Math.abs(t1.getBudget() - t2.getBudget()) <= 5000) {
            score += 25;
        }
        // Compare cleanliness levels (scale difference multiplied by 5)
        if (t1.getCleanlinessLevel() != null && t2.getCleanlinessLevel() != null) {
            score += (5 - Math.abs(t1.getCleanlinessLevel() - t2.getCleanlinessLevel())) * 5;
        }
        // Compare noise tolerance (scale difference multiplied by 5)
        if (t1.getNoiseTolerance() != null && t2.getNoiseTolerance() != null) {
            score += (5 - Math.abs(t1.getNoiseTolerance() - t2.getNoiseTolerance())) * 5;
        }
        // Cap the score at 100
        return Math.min(score, 100);
    }
}

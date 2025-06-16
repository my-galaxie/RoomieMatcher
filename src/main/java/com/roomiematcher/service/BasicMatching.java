package com.roomiematcher.service;

import com.roomiematcher.model.Tenant;
import org.springframework.stereotype.Service;

@Service
public class BasicMatching implements MatchingAlgorithm {

    @Override
    public double calculateMatchScore(Tenant t1, Tenant t2) {
        double score = 0;
        
        // First priority: Location matching (required)
        if (t1.getLocation() == null || t2.getLocation() == null ||
            !t1.getLocation().equalsIgnoreCase(t2.getLocation())) {
            return 0; // No match if locations don't match
        }
        
        // Second priority: Gender preference matching
        boolean genderMatch = true;
        
        // Check if t1 has gender preferences and if t2's gender is in those preferences
        if (t1.getPreferredGenders() != null && !t1.getPreferredGenders().isEmpty() &&
            t2.getGender() != null && !t1.getPreferredGenders().contains(t2.getGender())) {
            genderMatch = false;
        }
        
        // Check if t2 has gender preferences and if t1's gender is in those preferences
        if (t2.getPreferredGenders() != null && !t2.getPreferredGenders().isEmpty() &&
            t1.getGender() != null && !t2.getPreferredGenders().contains(t1.getGender())) {
            genderMatch = false;
        }
        
        // No match if gender preferences don't align
        if (!genderMatch) {
            return 0;
        }
        
        // +20 for matching city (already confirmed above)
        score += 20;
        
        // +25 if budget difference ≤ 5000
        if (t1.getBudget() != null && t2.getBudget() != null) {
            double budgetDiff = Math.abs(t1.getBudget() - t2.getBudget());
            if (budgetDiff <= 5000) {
                score += 25;
            } else if (budgetDiff <= 10000) {
                score += 15; // Partial points for close budget
            }
        }
        
        // +5 × (5 - |cleanliness diff|)
        if (t1.getCleanlinessLevel() != null && t2.getCleanlinessLevel() != null) {
            int cleanDiff = Math.abs(t1.getCleanlinessLevel() - t2.getCleanlinessLevel());
            score += 5 * (5 - cleanDiff);
        }
        
        // +5 × (5 - |noise diff|)
        if (t1.getNoiseTolerance() != null && t2.getNoiseTolerance() != null) {
            int noiseDiff = Math.abs(t1.getNoiseTolerance() - t2.getNoiseTolerance());
            score += 5 * (5 - noiseDiff);
        }
        
        // +5 for same smoking preference
        if (t1.getSmoking() != null && t2.getSmoking() != null && 
            t1.getSmoking().equals(t2.getSmoking())) {
            score += 5;
        }
        
        // +5 for same pet preference
        if (t1.getPets() != null && t2.getPets() != null && 
            t1.getPets().equals(t2.getPets())) {
            score += 5;
        }
        
        // +10 bonus for gender preference match
        if (t1.getGender() != null && t2.getGender() != null) {
            if ((t1.getPreferredGenders() != null && t1.getPreferredGenders().contains(t2.getGender())) ||
                (t2.getPreferredGenders() != null && t2.getPreferredGenders().contains(t1.getGender()))) {
                score += 10;
            }
        }
        
        // Cap the score at 100
        return Math.min(score, 100);
    }
}

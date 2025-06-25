package com.roomiematcher.match.service;

import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import org.springframework.stereotype.Service;

@Service
public class BasicMatchingAlgorithm implements MatchingAlgorithm {

    private static final double LOCATION_SCORE = 20.0;  
    private static final double GENDER_PREFERENCE_MATCH = 10.0;
    private static final double BUDGET_MATCH_HIGH = 25.0;
    private static final double BUDGET_MATCH_PARTIAL = 15.0;
    private static final double CLEANLINESS_MATCH_MAX = 25.0;
    private static final double NOISE_MATCH_MAX = 25.0;
    private static final double SMOKING_MATCH = 5.0;
    private static final double PETS_MATCH = 5.0;
    
    // Scores for the new preferences (total of 100 points)
    private static final double DAILY_SCHEDULE_MATCH = 6.0;
    private static final double GUEST_FREQUENCY_MATCH = 6.0;
    private static final double COOKING_HABITS_MATCH = 6.0;
    private static final double MUSIC_NOISE_MATCH = 8.0;
    private static final double CLEANING_FREQUENCY_MATCH = 8.0;
    private static final double SOCIAL_STYLE_MATCH = 7.0;
    private static final double TEMPERATURE_PREFERENCE_MATCH = 6.0;
    private static final double LIGHTING_PREFERENCE_MATCH = 5.0;
    private static final double PET_COMPATIBILITY_MATCH = 8.0;
    private static final double SMOKING_PREFERENCE_MATCH = 8.0;
    private static final double PARKING_NEEDS_MATCH = 5.0;
    private static final double OVERNIGHT_GUESTS_MATCH = 7.0;
    private static final double WORK_FROM_HOME_MATCH = 7.0;
    private static final double ALLERGIES_MATCH = 8.0;

    @Override
    public double calculateMatchScore(TenantProfileDTO t1, TenantProfileDTO t2) {
        double score = 0;
        double totalWeightedScore = 0;
        double totalWeight = LOCATION_SCORE + BUDGET_MATCH_HIGH + CLEANLINESS_MATCH_MAX + NOISE_MATCH_MAX +
                            SMOKING_MATCH + PETS_MATCH + GENDER_PREFERENCE_MATCH +
                            DAILY_SCHEDULE_MATCH + GUEST_FREQUENCY_MATCH + COOKING_HABITS_MATCH +
                            MUSIC_NOISE_MATCH + CLEANING_FREQUENCY_MATCH + SOCIAL_STYLE_MATCH +
                            TEMPERATURE_PREFERENCE_MATCH + LIGHTING_PREFERENCE_MATCH + PET_COMPATIBILITY_MATCH +
                            SMOKING_PREFERENCE_MATCH + PARKING_NEEDS_MATCH + OVERNIGHT_GUESTS_MATCH +
                            WORK_FROM_HOME_MATCH + ALLERGIES_MATCH;
        
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
        totalWeightedScore += LOCATION_SCORE;
        
        // +25 if budget difference ≤ 5000, or +15 if ≤ 10000
        if (t1.getBudget() != null && t2.getBudget() != null) {
            double budgetDiff = Math.abs(t1.getBudget() - t2.getBudget());
            if (budgetDiff <= 5000) {
                totalWeightedScore += BUDGET_MATCH_HIGH;
            } else if (budgetDiff <= 10000) {
                totalWeightedScore += BUDGET_MATCH_PARTIAL;
            }
        }
        
        // Cleanliness level match
        if (t1.getCleanlinessLevel() != null && t2.getCleanlinessLevel() != null) {
            int cleanDiff = Math.abs(t1.getCleanlinessLevel() - t2.getCleanlinessLevel());
            double cleanScore = CLEANLINESS_MATCH_MAX * (1 - (cleanDiff / 5.0));
            totalWeightedScore += cleanScore;
        }
        
        // Noise tolerance match
        if (t1.getNoiseTolerance() != null && t2.getNoiseTolerance() != null) {
            int noiseDiff = Math.abs(t1.getNoiseTolerance() - t2.getNoiseTolerance());
            double noiseScore = NOISE_MATCH_MAX * (1 - (noiseDiff / 5.0));
            totalWeightedScore += noiseScore;
        }
        
        // Smoking preference
        if (t1.getSmoking() != null && t2.getSmoking() != null && 
            t1.getSmoking().equals(t2.getSmoking())) {
            totalWeightedScore += SMOKING_MATCH;
        }
        
        // Pet preference
        if (t1.getPets() != null && t2.getPets() != null && 
            t1.getPets().equals(t2.getPets())) {
            totalWeightedScore += PETS_MATCH;
        }
        
        // Bonus for gender preference match
        if (t1.getGender() != null && t2.getGender() != null) {
            if ((t1.getPreferredGenders() != null && t1.getPreferredGenders().contains(t2.getGender())) ||
                (t2.getPreferredGenders() != null && t2.getPreferredGenders().contains(t1.getGender()))) {
                totalWeightedScore += GENDER_PREFERENCE_MATCH;
            }
        }
        
        // Daily schedule compatibility
        if (t1.getDailySchedule() != null && t2.getDailySchedule() != null) {
            totalWeightedScore += calculateEnumMatchScore(t1.getDailySchedule(), t2.getDailySchedule(), DAILY_SCHEDULE_MATCH);
        }
        
        // Guest frequency compatibility
        if (t1.getGuestFrequency() != null && t2.getGuestFrequency() != null) {
            totalWeightedScore += calculateGuestFrequencyMatchScore(t1.getGuestFrequency(), t2.getGuestFrequency(), GUEST_FREQUENCY_MATCH);
        }
        
        // Cooking habits compatibility
        if (t1.getCookingHabits() != null && t2.getCookingHabits() != null) {
            totalWeightedScore += calculateCookingHabitsMatchScore(t1.getCookingHabits(), t2.getCookingHabits(), COOKING_HABITS_MATCH);
        }
        
        // Music and noise compatibility
        if (t1.getMusicNoise() != null && t2.getMusicNoise() != null) {
            totalWeightedScore += calculateMusicNoiseMatchScore(t1.getMusicNoise(), t2.getMusicNoise(), MUSIC_NOISE_MATCH);
        }
        
        // Cleaning frequency compatibility
        if (t1.getCleaningFrequency() != null && t2.getCleaningFrequency() != null) {
            totalWeightedScore += calculateCleaningFrequencyMatchScore(t1.getCleaningFrequency(), t2.getCleaningFrequency(), CLEANING_FREQUENCY_MATCH);
        }
        
        // Social style compatibility
        if (t1.getSocialStyle() != null && t2.getSocialStyle() != null) {
            totalWeightedScore += calculateSocialStyleMatchScore(t1.getSocialStyle(), t2.getSocialStyle(), SOCIAL_STYLE_MATCH);
        }
        
        // Temperature preference compatibility
        if (t1.getTemperaturePreference() != null && t2.getTemperaturePreference() != null) {
            totalWeightedScore += calculateTemperatureMatchScore(t1.getTemperaturePreference(), t2.getTemperaturePreference(), TEMPERATURE_PREFERENCE_MATCH);
        }
        
        // Lighting preference compatibility
        if (t1.getLightingPreference() != null && t2.getLightingPreference() != null) {
            totalWeightedScore += calculateLightingMatchScore(t1.getLightingPreference(), t2.getLightingPreference(), LIGHTING_PREFERENCE_MATCH);
        }
        
        // Pet compatibility
        if (t1.getPetCompatibility() != null && t2.getPetCompatibility() != null) {
            totalWeightedScore += calculatePetCompatibilityScore(t1.getPetCompatibility(), t2.getPetCompatibility(), PET_COMPATIBILITY_MATCH);
        }
        
        // Smoking preference compatibility
        if (t1.getSmokingPreference() != null && t2.getSmokingPreference() != null) {
            totalWeightedScore += calculateSmokingPreferenceScore(t1.getSmokingPreference(), t2.getSmokingPreference(), SMOKING_PREFERENCE_MATCH);
        }
        
        // Parking needs compatibility
        if (t1.getParkingNeeds() != null && t2.getParkingNeeds() != null) {
            totalWeightedScore += calculateParkingNeedsScore(t1.getParkingNeeds(), t2.getParkingNeeds(), PARKING_NEEDS_MATCH);
        }
        
        // Overnight guests compatibility
        if (t1.getOvernightGuests() != null && t2.getOvernightGuests() != null) {
            totalWeightedScore += calculateOvernightGuestsScore(t1.getOvernightGuests(), t2.getOvernightGuests(), OVERNIGHT_GUESTS_MATCH);
        }
        
        // Work from home compatibility
        if (t1.getWorkFromHome() != null && t2.getWorkFromHome() != null) {
            totalWeightedScore += calculateWorkFromHomeScore(t1.getWorkFromHome(), t2.getWorkFromHome(), WORK_FROM_HOME_MATCH);
        }
        
        // Allergies compatibility
        if (t1.getAllergies() != null && t2.getAllergies() != null) {
            totalWeightedScore += calculateAllergiesScore(t1.getAllergies(), t2.getAllergies(), ALLERGIES_MATCH);
        }
        
        // Scale score to 100
        score = (totalWeightedScore / totalWeight) * 100;
        
        // Cap the score at 100
        return Math.min(score, 100);
    }
    
    private double calculateEnumMatchScore(String enum1, String enum2, double maxScore) {
        if (enum1.equals(enum2)) {
            return maxScore;
        } else if ((enum1.equals("FLEXIBLE") || enum2.equals("FLEXIBLE"))) {
            return maxScore * 0.75;
        } else {
            return 0;
        }
    }
    
    private double calculateGuestFrequencyMatchScore(String freq1, String freq2, double maxScore) {
        if (freq1.equals(freq2)) {
            return maxScore;
        }
        
        int diff = Math.abs(getFrequencyValue(freq1) - getFrequencyValue(freq2));
        return maxScore * (1 - (diff / 3.0));
    }
    
    private int getFrequencyValue(String frequency) {
        switch (frequency) {
            case "NONE": return 0;
            case "RARELY": return 1;
            case "SOMETIMES": return 2;
            case "OFTEN": return 3;
            default: return 0;
        }
    }
    
    private double calculateCookingHabitsMatchScore(String cooking1, String cooking2, double maxScore) {
        if (cooking1.equals(cooking2)) {
            return maxScore;
        }
        
        int diff = Math.abs(getCookingValue(cooking1) - getCookingValue(cooking2));
        return maxScore * (1 - (diff / 2.0));
    }
    
    private int getCookingValue(String cooking) {
        switch (cooking) {
            case "NONE": return 0;
            case "OCCASIONAL": return 1;
            case "FREQUENT": return 2;
            default: return 0;
        }
    }
    
    private double calculateMusicNoiseMatchScore(String noise1, String noise2, double maxScore) {
        if (noise1.equals(noise2)) {
            return maxScore;
        }
        
        int diff = Math.abs(getMusicNoiseValue(noise1) - getMusicNoiseValue(noise2));
        if (diff >= 3) {
            return 0; // Too much difference
        }
        return maxScore * (1 - (diff / 3.0));
    }
    
    private int getMusicNoiseValue(String noise) {
        switch (noise) {
            case "SILENT": return 0;
            case "LOW": return 1;
            case "MUSIC_LOVER": return 2;
            case "PARTY_FRIENDLY": return 3;
            default: return 0;
        }
    }
    
    private double calculateCleaningFrequencyMatchScore(String clean1, String clean2, double maxScore) {
        if (clean1.equals(clean2)) {
            return maxScore;
        }
        
        int diff = Math.abs(getCleaningValue(clean1) - getCleaningValue(clean2));
        return maxScore * (1 - (diff / 3.0));
    }
    
    private int getCleaningValue(String cleaning) {
        switch (cleaning) {
            case "ON_DEMAND": return 0;
            case "BIWEEKLY": return 1;
            case "WEEKLY": return 2;
            case "DAILY": return 3;
            default: return 0;
        }
    }
    
    private double calculateSocialStyleMatchScore(String social1, String social2, double maxScore) {
        if (social1.equals(social2)) {
            return maxScore;
        } else if (social1.equals("AMBIVERT") || social2.equals("AMBIVERT")) {
            return maxScore * 0.7;
        } else {
            return maxScore * 0.2; // Very different social styles
        }
    }
    
    private double calculateTemperatureMatchScore(String temp1, String temp2, double maxScore) {
        if (temp1.equals(temp2)) {
            return maxScore;
        } else if (temp1.equals("MODERATE") || temp2.equals("MODERATE")) {
            return maxScore * 0.7;
        } else {
            return maxScore * 0.2; // Very different preferences
        }
    }
    
    private double calculateLightingMatchScore(String light1, String light2, double maxScore) {
        if (light1.equals(light2)) {
            return maxScore;
        } else if (light1.equals("MODERATE") || light2.equals("MODERATE")) {
            return maxScore * 0.7;
        } else {
            return maxScore * 0.2; // Very different preferences
        }
    }
    
    private double calculatePetCompatibilityScore(String pet1, String pet2, double maxScore) {
        if (pet1.equals(pet2)) {
            return maxScore;
        } else if (pet1.equals("NONE") || pet2.equals("NONE")) {
            return maxScore * 0.5; // One person doesn't like pets
        } else {
            return maxScore * 0.7; // Different pet preferences but both like some pets
        }
    }
    
    private double calculateSmokingPreferenceScore(String smoking1, String smoking2, double maxScore) {
        if (smoking1.equals(smoking2)) {
            return maxScore;
        } else if (smoking1.equals("NON_SMOKER") && smoking2.equals("REGULAR")) {
            return 0; // Bad match
        } else if (smoking1.equals("REGULAR") && smoking2.equals("NON_SMOKER")) {
            return 0; // Bad match
        } else {
            return maxScore * 0.3; // One occasional, one other
        }
    }
    
    private double calculateParkingNeedsScore(String parking1, String parking2, double maxScore) {
        // For parking, the match is binary - either they need the same type of parking or not
        if (parking1.equals(parking2)) {
            return maxScore;
        } else if (parking1.equals("NONE") || parking2.equals("NONE")) {
            return maxScore * 0.8; // One doesn't need parking
        } else {
            return maxScore * 0.5; // Different parking needs
        }
    }
    
    private double calculateOvernightGuestsScore(String guests1, String guests2, double maxScore) {
        if (guests1.equals(guests2)) {
            return maxScore;
        }
        
        int diff = Math.abs(getOvernightValue(guests1) - getOvernightValue(guests2));
        if (diff >= 2) {
            return maxScore * 0.2; // Very different
        }
        return maxScore * (1 - (diff / 2.0));
    }
    
    private int getOvernightValue(String overnight) {
        switch (overnight) {
            case "NONE": return 0;
            case "OCCASIONAL": return 1;
            case "FREQUENT": return 2;
            default: return 0;
        }
    }
    
    private double calculateWorkFromHomeScore(String work1, String work2, double maxScore) {
        if (work1.equals(work2)) {
            return maxScore;
        }
        
        int diff = Math.abs(getWorkValue(work1) - getWorkValue(work2));
        return maxScore * (1 - (diff / 2.0));
    }
    
    private int getWorkValue(String work) {
        switch (work) {
            case "NONE": return 0;
            case "HYBRID": return 1;
            case "FULL_TIME": return 2;
            default: return 0;
        }
    }
    
    private double calculateAllergiesScore(String allergy1, String allergy2, double maxScore) {
        if (allergy1.equals(allergy2)) {
            return maxScore;
        } else if (allergy1.equals("NONE") || allergy2.equals("NONE")) {
            return maxScore * 0.8; // One has no allergies
        } else {
            // Check for compatible allergies
            boolean conflict = (allergy1.equals("PETS") && allergy2.equals("PETS")) || 
                               (allergy1.equals("POLLEN") && allergy2.equals("POLLEN")) ||
                               (allergy1.equals("FOOD") && allergy2.equals("FOOD"));
                              
            return conflict ? maxScore * 0.4 : maxScore * 0.7;
        }
    }
} 
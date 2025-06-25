package com.roomiematcher.common.dto.profile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantProfileRequest {
    
    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be greater than zero")
    private Double budget;
    
    @NotNull(message = "Location is required")
    private String location;
    
    @NotNull(message = "Cleanliness level is required")
    @Min(value = 1, message = "Cleanliness level must be between 1 and 5")
    @Max(value = 5, message = "Cleanliness level must be between 1 and 5")
    private Integer cleanlinessLevel;
    
    @NotNull(message = "Noise tolerance is required")
    @Min(value = 1, message = "Noise tolerance must be between 1 and 5")
    @Max(value = 5, message = "Noise tolerance must be between 1 and 5")
    private Integer noiseTolerance;
    
    @NotNull(message = "Smoking preference is required")
    private Boolean smoking;
    
    @NotNull(message = "Pets preference is required")
    private Boolean pets;
    
    private Set<String> preferredGenders;
    
    // New tenant preferences
    private String dailySchedule; // EARLY_BIRD, NIGHT_OWL, FLEXIBLE
    
    private String guestFrequency; // NONE, RARELY, SOMETIMES, OFTEN
    
    private String cookingHabits; // NONE, OCCASIONAL, FREQUENT
    
    private String musicNoise; // SILENT, LOW, MUSIC_LOVER, PARTY_FRIENDLY
    
    private String cleaningFrequency; // WEEKLY, BIWEEKLY, DAILY, ON_DEMAND
    
    private String socialStyle; // INTROVERT, AMBIVERT, EXTROVERT
    
    private String temperaturePreference; // COOL, MODERATE, WARM
    
    private String lightingPreference; // DIM, MODERATE, BRIGHT
    
    private String petCompatibility; // NONE, DOG_LOVER, CAT_LOVER, OTHER
    
    private String smokingPreference; // NON_SMOKER, OCCASIONAL, REGULAR
    
    private String parkingNeeds; // NONE, BIKE, CAR
    
    private String overnightGuests; // NONE, OCCASIONAL, FREQUENT
    
    private String workFromHome; // NONE, HYBRID, FULL_TIME
    
    private String allergies; // NONE, POLLEN, PETS, FOOD
} 
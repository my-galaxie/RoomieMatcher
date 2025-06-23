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
} 
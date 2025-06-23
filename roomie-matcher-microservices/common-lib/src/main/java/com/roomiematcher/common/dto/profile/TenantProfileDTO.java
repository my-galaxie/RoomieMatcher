package com.roomiematcher.common.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantProfileDTO {
    
    private Long id;
    private Long userId;
    private Double budget;
    private String location;
    private Integer cleanlinessLevel;
    private Integer noiseTolerance;
    private Boolean smoking;
    private Boolean pets;
    private Set<String> preferredGenders = new HashSet<>();
} 
package com.roomiematcher.common.dto.match;

import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    
    private Long id;
    private Long tenant1Id;
    private Long tenant2Id;
    private Double matchScore;
    private LocalDateTime createdAt;
    
    // Nested profiles for frontend convenience
    private TenantProfileDTO tenant1Profile;
    private TenantProfileDTO tenant2Profile;
}

package com.roomiematcher.common.dto.match;

import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import java.time.LocalDateTime;

public class MatchDTO {
    private Long id;
    private Long tenant1Id;
    private Long tenant2Id;
    private Double matchScore;
    private LocalDateTime createdAt;
    
    // Additional fields for display
    private String tenant1Name;
    private String tenant2Name;
    
    // Tenant profiles
    private TenantProfileDTO tenant1Profile;
    private TenantProfileDTO tenant2Profile;
    
    public MatchDTO() {
    }
    
    public MatchDTO(Long id, Long tenant1Id, Long tenant2Id, Double matchScore, LocalDateTime createdAt,
                   String tenant1Name, String tenant2Name, TenantProfileDTO tenant1Profile, TenantProfileDTO tenant2Profile) {
        this.id = id;
        this.tenant1Id = tenant1Id;
        this.tenant2Id = tenant2Id;
        this.matchScore = matchScore;
        this.createdAt = createdAt;
        this.tenant1Name = tenant1Name;
        this.tenant2Name = tenant2Name;
        this.tenant1Profile = tenant1Profile;
        this.tenant2Profile = tenant2Profile;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTenant1Id() {
        return tenant1Id;
    }
    
    public void setTenant1Id(Long tenant1Id) {
        this.tenant1Id = tenant1Id;
    }
    
    public Long getTenant2Id() {
        return tenant2Id;
    }
    
    public void setTenant2Id(Long tenant2Id) {
        this.tenant2Id = tenant2Id;
    }
    
    public Double getMatchScore() {
        return matchScore;
    }
    
    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getTenant1Name() {
        return tenant1Name;
    }
    
    public void setTenant1Name(String tenant1Name) {
        this.tenant1Name = tenant1Name;
    }
    
    public String getTenant2Name() {
        return tenant2Name;
    }
    
    public void setTenant2Name(String tenant2Name) {
        this.tenant2Name = tenant2Name;
    }
    
    public TenantProfileDTO getTenant1Profile() {
        return tenant1Profile;
    }
    
    public void setTenant1Profile(TenantProfileDTO tenant1Profile) {
        this.tenant1Profile = tenant1Profile;
    }
    
    public TenantProfileDTO getTenant2Profile() {
        return tenant2Profile;
    }
    
    public void setTenant2Profile(TenantProfileDTO tenant2Profile) {
        this.tenant2Profile = tenant2Profile;
    }
    
    // Builder pattern implementation
    public static MatchDTOBuilder builder() {
        return new MatchDTOBuilder();
    }
    
    public static class MatchDTOBuilder {
        private Long id;
        private Long tenant1Id;
        private Long tenant2Id;
        private Double matchScore;
        private LocalDateTime createdAt;
        private String tenant1Name;
        private String tenant2Name;
        private TenantProfileDTO tenant1Profile;
        private TenantProfileDTO tenant2Profile;
        
        public MatchDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public MatchDTOBuilder tenant1Id(Long tenant1Id) {
            this.tenant1Id = tenant1Id;
            return this;
        }
        
        public MatchDTOBuilder tenant2Id(Long tenant2Id) {
            this.tenant2Id = tenant2Id;
            return this;
        }
        
        public MatchDTOBuilder matchScore(Double matchScore) {
            this.matchScore = matchScore;
            return this;
        }
        
        public MatchDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public MatchDTOBuilder tenant1Name(String tenant1Name) {
            this.tenant1Name = tenant1Name;
            return this;
        }
        
        public MatchDTOBuilder tenant2Name(String tenant2Name) {
            this.tenant2Name = tenant2Name;
            return this;
        }
        
        public MatchDTOBuilder tenant1Profile(TenantProfileDTO tenant1Profile) {
            this.tenant1Profile = tenant1Profile;
            return this;
        }
        
        public MatchDTOBuilder tenant2Profile(TenantProfileDTO tenant2Profile) {
            this.tenant2Profile = tenant2Profile;
            return this;
        }
        
        public MatchDTO build() {
            return new MatchDTO(id, tenant1Id, tenant2Id, matchScore, createdAt, 
                              tenant1Name, tenant2Name, tenant1Profile, tenant2Profile);
        }
    }
}

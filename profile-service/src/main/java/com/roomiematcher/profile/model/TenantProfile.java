package com.roomiematcher.profile.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tenant_profiles")
public class TenantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "budget")
    private Double budget;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "cleanliness_level")
    private Integer cleanlinessLevel;
    
    @Column(name = "noise_tolerance")
    private Integer noiseTolerance;
    
    @Column(name = "smoking")
    private Boolean smoking;
    
    @Column(name = "pets")
    private Boolean pets;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "tenantProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PreferredGender> preferredGenders = new HashSet<>();
    
    public TenantProfile() {
    }
    
    public TenantProfile(Long id, Long userId, Double budget, String location, Integer cleanlinessLevel, 
                         Integer noiseTolerance, Boolean smoking, Boolean pets, String gender,
                         LocalDateTime createdAt, LocalDateTime updatedAt, Set<PreferredGender> preferredGenders) {
        this.id = id;
        this.userId = userId;
        this.budget = budget;
        this.location = location;
        this.cleanlinessLevel = cleanlinessLevel;
        this.noiseTolerance = noiseTolerance;
        this.smoking = smoking;
        this.pets = pets;
        this.gender = gender;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.preferredGenders = preferredGenders != null ? preferredGenders : new HashSet<>();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Utility methods for managing preferred genders
    public void addPreferredGender(String gender) {
        PreferredGender preferredGender = new PreferredGender();
        preferredGender.setGender(gender);
        preferredGender.setTenantProfile(this);
        this.preferredGenders.add(preferredGender);
    }
    
    public void removePreferredGender(String gender) {
        this.preferredGenders.removeIf(pg -> pg.getGender().equals(gender));
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Double getBudget() {
        return budget;
    }
    
    public void setBudget(Double budget) {
        this.budget = budget;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getCleanlinessLevel() {
        return cleanlinessLevel;
    }
    
    public void setCleanlinessLevel(Integer cleanlinessLevel) {
        this.cleanlinessLevel = cleanlinessLevel;
    }
    
    public Integer getNoiseTolerance() {
        return noiseTolerance;
    }
    
    public void setNoiseTolerance(Integer noiseTolerance) {
        this.noiseTolerance = noiseTolerance;
    }
    
    public Boolean getSmoking() {
        return smoking;
    }
    
    public void setSmoking(Boolean smoking) {
        this.smoking = smoking;
    }
    
    public Boolean getPets() {
        return pets;
    }
    
    public void setPets(Boolean pets) {
        this.pets = pets;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Set<PreferredGender> getPreferredGenders() {
        return preferredGenders;
    }
    
    public void setPreferredGenders(Set<PreferredGender> preferredGenders) {
        this.preferredGenders = preferredGenders;
    }
    
    // Builder pattern implementation
    public static TenantProfileBuilder builder() {
        return new TenantProfileBuilder();
    }
    
    public static class TenantProfileBuilder {
        private Long id;
        private Long userId;
        private Double budget;
        private String location;
        private Integer cleanlinessLevel;
        private Integer noiseTolerance;
        private Boolean smoking;
        private Boolean pets;
        private String gender;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Set<PreferredGender> preferredGenders = new HashSet<>();
        
        public TenantProfileBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public TenantProfileBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public TenantProfileBuilder budget(Double budget) {
            this.budget = budget;
            return this;
        }
        
        public TenantProfileBuilder location(String location) {
            this.location = location;
            return this;
        }
        
        public TenantProfileBuilder cleanlinessLevel(Integer cleanlinessLevel) {
            this.cleanlinessLevel = cleanlinessLevel;
            return this;
        }
        
        public TenantProfileBuilder noiseTolerance(Integer noiseTolerance) {
            this.noiseTolerance = noiseTolerance;
            return this;
        }
        
        public TenantProfileBuilder smoking(Boolean smoking) {
            this.smoking = smoking;
            return this;
        }
        
        public TenantProfileBuilder pets(Boolean pets) {
            this.pets = pets;
            return this;
        }
        
        public TenantProfileBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }
        
        public TenantProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public TenantProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public TenantProfileBuilder preferredGenders(Set<PreferredGender> preferredGenders) {
            this.preferredGenders = preferredGenders;
            return this;
        }
        
        public TenantProfile build() {
            return new TenantProfile(id, userId, budget, location, cleanlinessLevel, noiseTolerance, 
                                    smoking, pets, gender, createdAt, updatedAt, preferredGenders);
        }
    }
} 
package com.roomiematcher.profile.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tenant_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
} 
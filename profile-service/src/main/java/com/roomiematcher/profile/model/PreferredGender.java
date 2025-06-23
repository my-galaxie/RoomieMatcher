package com.roomiematcher.profile.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tenant_preferred_genders")
public class PreferredGender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantProfile tenantProfile;
    
    public PreferredGender() {
    }
    
    public PreferredGender(Long id, String gender, TenantProfile tenantProfile) {
        this.id = id;
        this.gender = gender;
        this.tenantProfile = tenantProfile;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public TenantProfile getTenantProfile() {
        return tenantProfile;
    }
    
    public void setTenantProfile(TenantProfile tenantProfile) {
        this.tenantProfile = tenantProfile;
    }
    
    // Builder pattern implementation
    public static PreferredGenderBuilder builder() {
        return new PreferredGenderBuilder();
    }
    
    public static class PreferredGenderBuilder {
        private Long id;
        private String gender;
        private TenantProfile tenantProfile;
        
        public PreferredGenderBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public PreferredGenderBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }
        
        public PreferredGenderBuilder tenantProfile(TenantProfile tenantProfile) {
            this.tenantProfile = tenantProfile;
            return this;
        }
        
        public PreferredGender build() {
            return new PreferredGender(id, gender, tenantProfile);
        }
    }
} 
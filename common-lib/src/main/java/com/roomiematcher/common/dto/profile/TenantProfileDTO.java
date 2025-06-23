package com.roomiematcher.common.dto.profile;

import java.util.Set;

public class TenantProfileDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String gender;
    private Double budget;
    private String location;
    private Integer cleanlinessLevel;
    private Integer noiseTolerance;
    private Boolean smoking;
    private Boolean pets;
    private Set<String> preferredGenders;
    
    public TenantProfileDTO() {
    }
    
    public TenantProfileDTO(Long id, Long userId, String name, String email, String gender, Double budget, 
                           String location, Integer cleanlinessLevel, Integer noiseTolerance, 
                           Boolean smoking, Boolean pets, Set<String> preferredGenders) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.budget = budget;
        this.location = location;
        this.cleanlinessLevel = cleanlinessLevel;
        this.noiseTolerance = noiseTolerance;
        this.smoking = smoking;
        this.pets = pets;
        this.preferredGenders = preferredGenders;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
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
    
    public Set<String> getPreferredGenders() {
        return preferredGenders;
    }
    
    public void setPreferredGenders(Set<String> preferredGenders) {
        this.preferredGenders = preferredGenders;
    }
    
    // Builder pattern implementation
    public static TenantProfileDTOBuilder builder() {
        return new TenantProfileDTOBuilder();
    }
    
    public static class TenantProfileDTOBuilder {
        private Long id;
        private Long userId;
        private String name;
        private String email;
        private String gender;
        private Double budget;
        private String location;
        private Integer cleanlinessLevel;
        private Integer noiseTolerance;
        private Boolean smoking;
        private Boolean pets;
        private Set<String> preferredGenders;
        
        public TenantProfileDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public TenantProfileDTOBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public TenantProfileDTOBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public TenantProfileDTOBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public TenantProfileDTOBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }
        
        public TenantProfileDTOBuilder budget(Double budget) {
            this.budget = budget;
            return this;
        }
        
        public TenantProfileDTOBuilder location(String location) {
            this.location = location;
            return this;
        }
        
        public TenantProfileDTOBuilder cleanlinessLevel(Integer cleanlinessLevel) {
            this.cleanlinessLevel = cleanlinessLevel;
            return this;
        }
        
        public TenantProfileDTOBuilder noiseTolerance(Integer noiseTolerance) {
            this.noiseTolerance = noiseTolerance;
            return this;
        }
        
        public TenantProfileDTOBuilder smoking(Boolean smoking) {
            this.smoking = smoking;
            return this;
        }
        
        public TenantProfileDTOBuilder pets(Boolean pets) {
            this.pets = pets;
            return this;
        }
        
        public TenantProfileDTOBuilder preferredGenders(Set<String> preferredGenders) {
            this.preferredGenders = preferredGenders;
            return this;
        }
        
        public TenantProfileDTO build() {
            return new TenantProfileDTO(id, userId, name, email, gender, budget, location, 
                                      cleanlinessLevel, noiseTolerance, smoking, pets, preferredGenders);
        }
    }
} 
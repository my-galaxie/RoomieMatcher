package com.roomiematcher.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("TENANT")
public class Tenant extends User {

    private Double budget;
    private String location;
    private Integer cleanlinessLevel;
    private Integer noiseTolerance;
    private Boolean smoking;
    private Boolean pets;
    
    @ElementCollection
    @CollectionTable(name = "tenant_preferred_genders", joinColumns = @JoinColumn(name = "tenant_id"))
    @Column(name = "preferred_gender")
    private Set<String> preferredGenders = new HashSet<>();

    // Constructors
    public Tenant() {
        super();
    }

    public Tenant(String name, String email, String password) {
        super(name, email, password);
    }

    // Getters and Setters
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
    
    public void addPreferredGender(String gender) {
        if (this.preferredGenders == null) {
            this.preferredGenders = new HashSet<>();
        }
        this.preferredGenders.add(gender);
    }
    
    public void removePreferredGender(String gender) {
        if (this.preferredGenders != null) {
            this.preferredGenders.remove(gender);
        }
    }
}

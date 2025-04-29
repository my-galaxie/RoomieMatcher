package com.roomiematcher.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TENANT")
public class Tenant extends User {

    private Double budget;
    private String location;
    private Boolean smoking;
    private Boolean pets;
    private Integer cleanlinessLevel;
    private Integer noiseTolerance;

    // No-args constructor required by JPA
    public Tenant() {
        super();
    }

    // Parameterized constructor to initialize fields
    public Tenant(String name, String email, String password, Double budget, String location,
                  Boolean smoking, Boolean pets, Integer cleanlinessLevel, Integer noiseTolerance) {
        super(name, email, password);
        this.budget = budget;
        this.location = location;
        this.smoking = smoking;
        this.pets = pets;
        this.cleanlinessLevel = cleanlinessLevel;
        this.noiseTolerance = noiseTolerance;
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
}

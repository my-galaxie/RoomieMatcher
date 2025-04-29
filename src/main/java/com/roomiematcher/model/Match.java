package com.roomiematcher.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Tenant tenant1;
    
    @ManyToOne
    private Tenant tenant2;
    
    private Double matchScore;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors, getters, and setters
    public Match() {}

    public Match(Tenant tenant1, Tenant tenant2, Double matchScore) {
        this.tenant1 = tenant1;
        this.tenant2 = tenant2;
        this.matchScore = matchScore;
    }

    public Long getId() { return id; }
    public Tenant getTenant1() { return tenant1; }
    public void setTenant1(Tenant tenant1) { this.tenant1 = tenant1; }
    public Tenant getTenant2() { return tenant2; }
    public void setTenant2(Tenant tenant2) { this.tenant2 = tenant2; }
    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

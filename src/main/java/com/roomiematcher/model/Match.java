package com.roomiematcher.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant1_id")
    private Tenant tenant1;

    @ManyToOne
    @JoinColumn(name = "tenant2_id")
    private Tenant tenant2;

    @Column(name = "match_score")
    private Double matchScore;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Match() {
    }

    public Match(Tenant tenant1, Tenant tenant2, Double matchScore) {
        this.tenant1 = tenant1;
        this.tenant2 = tenant2;
        this.matchScore = matchScore;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant1() {
        return tenant1;
    }

    public void setTenant1(Tenant tenant1) {
        this.tenant1 = tenant1;
    }

    public Tenant getTenant2() {
        return tenant2;
    }

    public void setTenant2(Tenant tenant2) {
        this.tenant2 = tenant2;
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
}

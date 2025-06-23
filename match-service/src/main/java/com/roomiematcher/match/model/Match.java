package com.roomiematcher.match.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant1_id", nullable = false)
    private Long tenant1Id;
    
    @Column(name = "tenant2_id", nullable = false)
    private Long tenant2Id;
    
    @Column(name = "match_score")
    private Double matchScore;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public Match() {
    }
    
    public Match(Long id, Long tenant1Id, Long tenant2Id, Double matchScore, LocalDateTime createdAt) {
        this.id = id;
        this.tenant1Id = tenant1Id;
        this.tenant2Id = tenant2Id;
        this.matchScore = matchScore;
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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
    
    // Builder pattern implementation
    public static MatchBuilder builder() {
        return new MatchBuilder();
    }
    
    public static class MatchBuilder {
        private Long id;
        private Long tenant1Id;
        private Long tenant2Id;
        private Double matchScore;
        private LocalDateTime createdAt;
        
        public MatchBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public MatchBuilder tenant1Id(Long tenant1Id) {
            this.tenant1Id = tenant1Id;
            return this;
        }
        
        public MatchBuilder tenant2Id(Long tenant2Id) {
            this.tenant2Id = tenant2Id;
            return this;
        }
        
        public MatchBuilder matchScore(Double matchScore) {
            this.matchScore = matchScore;
            return this;
        }
        
        public MatchBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Match build() {
            return new Match(id, tenant1Id, tenant2Id, matchScore, createdAt);
        }
    }
}

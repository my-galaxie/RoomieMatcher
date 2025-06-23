package com.roomiematcher.match.repository;

import com.roomiematcher.match.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findByTenant1Id(Long tenantId);
    
    List<Match> findByTenant2Id(Long tenantId);
    
    @Query("SELECT m FROM Match m WHERE m.tenant1Id = :tenantId OR m.tenant2Id = :tenantId ORDER BY m.matchScore DESC")
    List<Match> findAllMatchesForTenant(Long tenantId);
    
    @Query("SELECT m FROM Match m WHERE (m.tenant1Id = :tenant1Id AND m.tenant2Id = :tenant2Id) OR " +
           "(m.tenant1Id = :tenant2Id AND m.tenant2Id = :tenant1Id)")
    Match findMatchBetweenTenants(Long tenant1Id, Long tenant2Id);
} 
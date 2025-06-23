package com.roomiematcher.match.service;

import com.roomiematcher.common.dto.profile.TenantProfileDTO;

/**
 * This interface defines the contract for a matching algorithm
 * that calculates a compatibility score between two Tenant profiles.
 */
public interface MatchingAlgorithm {
    
    /**
     * Calculates the compatibility score between two tenant profiles.
     * 
     * @param tenant1 the first tenant profile
     * @param tenant2 the second tenant profile
     * @return a double value representing the match score (e.g., 0 to 100)
     */
    double calculateMatchScore(TenantProfileDTO tenant1, TenantProfileDTO tenant2);
} 
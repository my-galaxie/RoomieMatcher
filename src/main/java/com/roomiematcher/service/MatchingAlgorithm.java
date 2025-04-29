package com.roomiematcher.service;

import com.roomiematcher.model.Tenant;

/**
 * This interface defines the contract for a matching algorithm
 * that calculates a compatibility score between two Tenant objects.
 */
public interface MatchingAlgorithm {
    
    /**
     * Calculates the compatibility score between two tenants.
     * 
     * @param t1 the first tenant
     * @param t2 the second tenant
     * @return a double value representing the match score (e.g., 0 to 100)
     */
    double calculateMatchScore(Tenant t1, Tenant t2);
}

package com.roomiematcher.match.service;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.match.MatchDTO;
import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import com.roomiematcher.common.exception.ResourceNotFoundException;
import com.roomiematcher.match.client.ProfileClient;
import com.roomiematcher.match.model.Match;
import com.roomiematcher.match.repository.MatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepository;
    private final MatchingAlgorithm matchingAlgorithm;
    private final ProfileClient profileClient;
    
    public MatchService(MatchRepository matchRepository, MatchingAlgorithm matchingAlgorithm, 
                       ProfileClient profileClient) {
        this.matchRepository = matchRepository;
        this.matchingAlgorithm = matchingAlgorithm;
        this.profileClient = profileClient;
    }

    /**
     * Finds potential matches for a tenant based on their profile and compatibility scores.
     * 
     * @param userId the ID of the user/tenant
     * @return a list of match DTOs with compatibility scores
     */
    public List<MatchDTO> findPotentialMatches(Long userId) {
        // Get the tenant profile
        TenantProfileDTO userProfile;
        try {
            ApiResponse<TenantProfileDTO> response = profileClient.getProfileByUserId(userId);
            userProfile = response.getData();
            if (userProfile == null) {
                throw new ResourceNotFoundException("TenantProfile", "userId", userId);
            }
        } catch (Exception e) {
            logger.error("Failed to get user profile for userId: {}", userId, e);
            return Collections.emptyList();
        }

        // Get all potential matches
        List<TenantProfileDTO> allProfiles;
        try {
            ApiResponse<List<TenantProfileDTO>> response = profileClient.getAllProfiles();
            allProfiles = response.getData();
            if (allProfiles == null) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Failed to get all profiles", e);
            return Collections.emptyList();
        }

        // Remove the user's own profile
        allProfiles = allProfiles.stream()
                .filter(profile -> !profile.getUserId().equals(userId))
                .collect(Collectors.toList());

        List<MatchDTO> potentialMatches = new ArrayList<>();

        // Calculate match scores and create match records
        for (TenantProfileDTO otherProfile : allProfiles) {
            // Calculate match score
            double score = matchingAlgorithm.calculateMatchScore(userProfile, otherProfile);
            
            if (score > 0) { // Only consider non-zero scores
                // Check if a match record already exists
                Match existingMatch = matchRepository.findMatchBetweenTenants(
                        userProfile.getUserId(), otherProfile.getUserId());
                
                Match match;
                if (existingMatch != null) {
                    // Update existing match
                    existingMatch.setMatchScore(score);
                    match = matchRepository.save(existingMatch);
                } else {
                    // Create a new match
                    match = Match.builder()
                            .tenant1Id(userProfile.getUserId())
                            .tenant2Id(otherProfile.getUserId())
                            .matchScore(score)
                            .build();
                    match = matchRepository.save(match);
                }
                
                // Convert to DTO
                MatchDTO matchDTO = convertToDTO(match);
                matchDTO.setTenant1Profile(userProfile);
                matchDTO.setTenant2Profile(otherProfile);
                
                potentialMatches.add(matchDTO);
            }
        }
        
        // Sort by match score (highest first)
        potentialMatches.sort((m1, m2) -> Double.compare(m2.getMatchScore(), m1.getMatchScore()));
        
        return potentialMatches;
    }
    
    /**
     * Gets all matches for a tenant
     */
    public List<MatchDTO> getMatchesForTenant(Long userId) {
        // Get tenant profile
        TenantProfileDTO userProfile;
        try {
            ApiResponse<TenantProfileDTO> response = profileClient.getProfileByUserId(userId);
            userProfile = response.getData();
            if (userProfile == null) {
                throw new ResourceNotFoundException("TenantProfile", "userId", userId);
            }
        } catch (Exception e) {
            logger.error("Failed to get user profile for userId: {}", userId, e);
            throw new ResourceNotFoundException("TenantProfile", "userId", userId);
        }
        
        // Get all matches for the tenant
        List<Match> matches = matchRepository.findAllMatchesForTenant(userId);
        if (matches.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Prepare result list
        List<MatchDTO> matchDTOs = new ArrayList<>();
        
        // Convert matches to DTOs and fetch associated profiles
        for (Match match : matches) {
            MatchDTO matchDTO = convertToDTO(match);
            
            // Fetch tenant profiles
            Long otherTenantId = match.getTenant1Id().equals(userId) ? 
                    match.getTenant2Id() : match.getTenant1Id();
                    
            try {
                ApiResponse<TenantProfileDTO> otherProfileResponse = 
                        profileClient.getProfileByUserId(otherTenantId);
                TenantProfileDTO otherProfile = otherProfileResponse.getData();
                
                // Set profiles in the DTO
                if (match.getTenant1Id().equals(userId)) {
                    matchDTO.setTenant1Profile(userProfile);
                    matchDTO.setTenant2Profile(otherProfile);
                } else {
                    matchDTO.setTenant1Profile(otherProfile);
                    matchDTO.setTenant2Profile(userProfile);
                }
                
                matchDTOs.add(matchDTO);
            } catch (Exception e) {
                logger.error("Failed to fetch profile for tenant ID: {}", otherTenantId, e);
                // Continue to next match
            }
        }
        
        // Sort by match score (highest first)
        matchDTOs.sort((m1, m2) -> Double.compare(m2.getMatchScore(), m1.getMatchScore()));
        
        return matchDTOs;
    }
    
    /**
     * Gets a specific match by ID
     */
    public MatchDTO getMatchById(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match", "id", matchId));
        
        MatchDTO matchDTO = convertToDTO(match);
        
        // Fetch tenant profiles
        try {
            ApiResponse<TenantProfileDTO> tenant1Response = 
                    profileClient.getProfileByUserId(match.getTenant1Id());
            ApiResponse<TenantProfileDTO> tenant2Response = 
                    profileClient.getProfileByUserId(match.getTenant2Id());
            
            matchDTO.setTenant1Profile(tenant1Response.getData());
            matchDTO.setTenant2Profile(tenant2Response.getData());
        } catch (Exception e) {
            logger.error("Failed to fetch tenant profiles for match ID: {}", matchId, e);
        }
        
        return matchDTO;
    }
    
    /**
     * Convert entity to DTO
     */
    private MatchDTO convertToDTO(Match match) {
        return MatchDTO.builder()
                .id(match.getId())
                .tenant1Id(match.getTenant1Id())
                .tenant2Id(match.getTenant2Id())
                .matchScore(match.getMatchScore())
                .createdAt(match.getCreatedAt())
                .build();
    }
}

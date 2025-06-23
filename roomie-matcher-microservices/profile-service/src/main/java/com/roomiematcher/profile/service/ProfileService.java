package com.roomiematcher.profile.service;

import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import com.roomiematcher.common.exception.ResourceNotFoundException;
import com.roomiematcher.profile.model.PreferredGender;
import com.roomiematcher.profile.model.TenantProfile;
import com.roomiematcher.profile.repository.PreferredGenderRepository;
import com.roomiematcher.profile.repository.TenantProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final TenantProfileRepository tenantProfileRepository;
    private final PreferredGenderRepository preferredGenderRepository;
    
    /**
     * Create a new tenant profile
     */
    @Transactional
    public TenantProfileDTO createProfile(Long userId, TenantProfileDTO profileDTO) {
        if (tenantProfileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Profile already exists for user: " + userId);
        }
        
        TenantProfile profile = TenantProfile.builder()
                .userId(userId)
                .budget(profileDTO.getBudget())
                .location(profileDTO.getLocation())
                .cleanlinessLevel(profileDTO.getCleanlinessLevel())
                .noiseTolerance(profileDTO.getNoiseTolerance())
                .smoking(profileDTO.getSmoking())
                .pets(profileDTO.getPets())
                .gender(profileDTO.getGender())
                .build();
        
        TenantProfile savedProfile = tenantProfileRepository.save(profile);
        
        // Add preferred genders
        if (profileDTO.getPreferredGenders() != null && !profileDTO.getPreferredGenders().isEmpty()) {
            profileDTO.getPreferredGenders().forEach(savedProfile::addPreferredGender);
            tenantProfileRepository.save(savedProfile);
        }
        
        return convertToDTO(savedProfile);
    }
    
    /**
     * Get a profile by user ID
     */
    public TenantProfileDTO getProfileByUserId(Long userId) {
        TenantProfile profile = tenantProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TenantProfile", "userId", userId));
        
        return convertToDTO(profile);
    }
    
    /**
     * Update a tenant profile
     */
    @Transactional
    public TenantProfileDTO updateProfile(Long userId, TenantProfileDTO profileDTO) {
        TenantProfile profile = tenantProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TenantProfile", "userId", userId));
        
        profile.setBudget(profileDTO.getBudget());
        profile.setLocation(profileDTO.getLocation());
        profile.setCleanlinessLevel(profileDTO.getCleanlinessLevel());
        profile.setNoiseTolerance(profileDTO.getNoiseTolerance());
        profile.setSmoking(profileDTO.getSmoking());
        profile.setPets(profileDTO.getPets());
        profile.setGender(profileDTO.getGender());
        
        // Update preferred genders
        if (profileDTO.getPreferredGenders() != null) {
            // Clear existing preferred genders
            profile.getPreferredGenders().clear();
            
            // Add new preferred genders
            profileDTO.getPreferredGenders().forEach(profile::addPreferredGender);
        }
        
        TenantProfile updatedProfile = tenantProfileRepository.save(profile);
        return convertToDTO(updatedProfile);
    }
    
    /**
     * Get profiles by location
     */
    public List<TenantProfileDTO> getProfilesByLocation(String location) {
        List<TenantProfile> profiles = tenantProfileRepository.findByLocation(location);
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all profiles
     */
    public List<TenantProfileDTO> getAllProfiles() {
        List<TenantProfile> profiles = tenantProfileRepository.findAll();
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a profile
     */
    @Transactional
    public void deleteProfile(Long userId) {
        TenantProfile profile = tenantProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TenantProfile", "userId", userId));
        
        tenantProfileRepository.delete(profile);
    }
    
    /**
     * Convert entity to DTO
     */
    private TenantProfileDTO convertToDTO(TenantProfile profile) {
        Set<String> preferredGenders = profile.getPreferredGenders().stream()
                .map(PreferredGender::getGender)
                .collect(Collectors.toSet());
                
        return TenantProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .budget(profile.getBudget())
                .location(profile.getLocation())
                .cleanlinessLevel(profile.getCleanlinessLevel())
                .noiseTolerance(profile.getNoiseTolerance())
                .smoking(profile.getSmoking())
                .pets(profile.getPets())
                .preferredGenders(preferredGenders)
                .build();
    }
} 
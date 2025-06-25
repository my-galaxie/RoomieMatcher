package com.roomiematcher.profile.service;

import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import com.roomiematcher.common.exception.ResourceNotFoundException;
import com.roomiematcher.profile.model.*;
import com.roomiematcher.profile.repository.PreferredGenderRepository;
import com.roomiematcher.profile.repository.TenantProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final TenantProfileRepository tenantProfileRepository;
    private final PreferredGenderRepository preferredGenderRepository;
    
    public ProfileService(TenantProfileRepository tenantProfileRepository, 
                         PreferredGenderRepository preferredGenderRepository) {
        this.tenantProfileRepository = tenantProfileRepository;
        this.preferredGenderRepository = preferredGenderRepository;
    }
    
    /**
     * Create a new tenant profile
     */
    @Transactional
    public TenantProfileDTO createProfile(Long userId, TenantProfileDTO profileDTO) {
        if (tenantProfileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Profile already exists for user: " + userId);
        }
        
        TenantProfile.TenantProfileBuilder builder = TenantProfile.builder()
                .userId(userId)
                .budget(profileDTO.getBudget())
                .location(profileDTO.getLocation())
                .cleanlinessLevel(profileDTO.getCleanlinessLevel())
                .noiseTolerance(profileDTO.getNoiseTolerance())
                .smoking(profileDTO.getSmoking())
                .pets(profileDTO.getPets())
                .gender(profileDTO.getGender());
                
        // Set new tenant preferences
        if (profileDTO.getDailySchedule() != null) {
            builder.dailySchedule(DailySchedule.valueOf(profileDTO.getDailySchedule()));
        }
        
        if (profileDTO.getGuestFrequency() != null) {
            builder.guestFrequency(GuestFrequency.valueOf(profileDTO.getGuestFrequency()));
        }
        
        if (profileDTO.getCookingHabits() != null) {
            builder.cookingHabits(CookingHabits.valueOf(profileDTO.getCookingHabits()));
        }
        
        if (profileDTO.getMusicNoise() != null) {
            builder.musicNoise(MusicNoise.valueOf(profileDTO.getMusicNoise()));
        }
        
        if (profileDTO.getCleaningFrequency() != null) {
            builder.cleaningFrequency(CleaningFrequency.valueOf(profileDTO.getCleaningFrequency()));
        }
        
        if (profileDTO.getSocialStyle() != null) {
            builder.socialStyle(SocialStyle.valueOf(profileDTO.getSocialStyle()));
        }
        
        if (profileDTO.getTemperaturePreference() != null) {
            builder.temperaturePreference(TemperaturePreference.valueOf(profileDTO.getTemperaturePreference()));
        }
        
        if (profileDTO.getLightingPreference() != null) {
            builder.lightingPreference(LightingPreference.valueOf(profileDTO.getLightingPreference()));
        }
        
        if (profileDTO.getPetCompatibility() != null) {
            builder.petCompatibility(PetCompatibility.valueOf(profileDTO.getPetCompatibility()));
        }
        
        if (profileDTO.getSmokingPreference() != null) {
            builder.smokingPreference(SmokingPreference.valueOf(profileDTO.getSmokingPreference()));
        }
        
        if (profileDTO.getParkingNeeds() != null) {
            builder.parkingNeeds(ParkingNeeds.valueOf(profileDTO.getParkingNeeds()));
        }
        
        if (profileDTO.getOvernightGuests() != null) {
            builder.overnightGuests(OvernightGuests.valueOf(profileDTO.getOvernightGuests()));
        }
        
        if (profileDTO.getWorkFromHome() != null) {
            builder.workFromHome(WorkFromHome.valueOf(profileDTO.getWorkFromHome()));
        }
        
        if (profileDTO.getAllergies() != null) {
            builder.allergies(Allergies.valueOf(profileDTO.getAllergies()));
        }
        
        TenantProfile profile = builder.build();
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
        
        // Update new tenant preferences
        if (profileDTO.getDailySchedule() != null) {
            profile.setDailySchedule(DailySchedule.valueOf(profileDTO.getDailySchedule()));
        }
        
        if (profileDTO.getGuestFrequency() != null) {
            profile.setGuestFrequency(GuestFrequency.valueOf(profileDTO.getGuestFrequency()));
        }
        
        if (profileDTO.getCookingHabits() != null) {
            profile.setCookingHabits(CookingHabits.valueOf(profileDTO.getCookingHabits()));
        }
        
        if (profileDTO.getMusicNoise() != null) {
            profile.setMusicNoise(MusicNoise.valueOf(profileDTO.getMusicNoise()));
        }
        
        if (profileDTO.getCleaningFrequency() != null) {
            profile.setCleaningFrequency(CleaningFrequency.valueOf(profileDTO.getCleaningFrequency()));
        }
        
        if (profileDTO.getSocialStyle() != null) {
            profile.setSocialStyle(SocialStyle.valueOf(profileDTO.getSocialStyle()));
        }
        
        if (profileDTO.getTemperaturePreference() != null) {
            profile.setTemperaturePreference(TemperaturePreference.valueOf(profileDTO.getTemperaturePreference()));
        }
        
        if (profileDTO.getLightingPreference() != null) {
            profile.setLightingPreference(LightingPreference.valueOf(profileDTO.getLightingPreference()));
        }
        
        if (profileDTO.getPetCompatibility() != null) {
            profile.setPetCompatibility(PetCompatibility.valueOf(profileDTO.getPetCompatibility()));
        }
        
        if (profileDTO.getSmokingPreference() != null) {
            profile.setSmokingPreference(SmokingPreference.valueOf(profileDTO.getSmokingPreference()));
        }
        
        if (profileDTO.getParkingNeeds() != null) {
            profile.setParkingNeeds(ParkingNeeds.valueOf(profileDTO.getParkingNeeds()));
        }
        
        if (profileDTO.getOvernightGuests() != null) {
            profile.setOvernightGuests(OvernightGuests.valueOf(profileDTO.getOvernightGuests()));
        }
        
        if (profileDTO.getWorkFromHome() != null) {
            profile.setWorkFromHome(WorkFromHome.valueOf(profileDTO.getWorkFromHome()));
        }
        
        if (profileDTO.getAllergies() != null) {
            profile.setAllergies(Allergies.valueOf(profileDTO.getAllergies()));
        }
        
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
                
        TenantProfileDTO.TenantProfileDTOBuilder builder = TenantProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .budget(profile.getBudget())
                .location(profile.getLocation())
                .cleanlinessLevel(profile.getCleanlinessLevel())
                .noiseTolerance(profile.getNoiseTolerance())
                .smoking(profile.getSmoking())
                .pets(profile.getPets())
                .preferredGenders(preferredGenders);
                
        // Convert enums to strings
        if (profile.getDailySchedule() != null) {
            builder.dailySchedule(profile.getDailySchedule().name());
        }
        
        if (profile.getGuestFrequency() != null) {
            builder.guestFrequency(profile.getGuestFrequency().name());
        }
        
        if (profile.getCookingHabits() != null) {
            builder.cookingHabits(profile.getCookingHabits().name());
        }
        
        if (profile.getMusicNoise() != null) {
            builder.musicNoise(profile.getMusicNoise().name());
        }
        
        if (profile.getCleaningFrequency() != null) {
            builder.cleaningFrequency(profile.getCleaningFrequency().name());
        }
        
        if (profile.getSocialStyle() != null) {
            builder.socialStyle(profile.getSocialStyle().name());
        }
        
        if (profile.getTemperaturePreference() != null) {
            builder.temperaturePreference(profile.getTemperaturePreference().name());
        }
        
        if (profile.getLightingPreference() != null) {
            builder.lightingPreference(profile.getLightingPreference().name());
        }
        
        if (profile.getPetCompatibility() != null) {
            builder.petCompatibility(profile.getPetCompatibility().name());
        }
        
        if (profile.getSmokingPreference() != null) {
            builder.smokingPreference(profile.getSmokingPreference().name());
        }
        
        if (profile.getParkingNeeds() != null) {
            builder.parkingNeeds(profile.getParkingNeeds().name());
        }
        
        if (profile.getOvernightGuests() != null) {
            builder.overnightGuests(profile.getOvernightGuests().name());
        }
        
        if (profile.getWorkFromHome() != null) {
            builder.workFromHome(profile.getWorkFromHome().name());
        }
        
        if (profile.getAllergies() != null) {
            builder.allergies(profile.getAllergies().name());
        }
                
        return builder.build();
    }
} 
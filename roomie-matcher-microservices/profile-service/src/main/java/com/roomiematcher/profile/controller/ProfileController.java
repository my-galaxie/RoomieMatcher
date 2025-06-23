package com.roomiematcher.profile.controller;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import com.roomiematcher.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "APIs for tenant profile management")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @Operation(summary = "Create a new tenant profile")
    public ResponseEntity<ApiResponse<TenantProfileDTO>> createProfile(
            @RequestParam Long userId,
            @Valid @RequestBody TenantProfileDTO profileDTO) {
        
        TenantProfileDTO createdProfile = profileService.createProfile(userId, profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created successfully", createdProfile));
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get a tenant profile by user ID")
    public ResponseEntity<ApiResponse<TenantProfileDTO>> getProfileByUserId(@PathVariable Long userId) {
        TenantProfileDTO profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update a tenant profile")
    public ResponseEntity<ApiResponse<TenantProfileDTO>> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody TenantProfileDTO profileDTO) {
        
        TenantProfileDTO updatedProfile = profileService.updateProfile(userId, profileDTO);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
    }
    
    @GetMapping("/location/{location}")
    @Operation(summary = "Get profiles by location")
    public ResponseEntity<ApiResponse<List<TenantProfileDTO>>> getProfilesByLocation(@PathVariable String location) {
        List<TenantProfileDTO> profiles = profileService.getProfilesByLocation(location);
        return ResponseEntity.ok(ApiResponse.success(profiles));
    }
    
    @GetMapping
    @Operation(summary = "Get all profiles")
    public ResponseEntity<ApiResponse<List<TenantProfileDTO>>> getAllProfiles() {
        List<TenantProfileDTO> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(ApiResponse.success(profiles));
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a tenant profile")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable Long userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile deleted successfully", null));
    }
} 
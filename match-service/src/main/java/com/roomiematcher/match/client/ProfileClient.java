package com.roomiematcher.match.client;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.profile.TenantProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "profile-service", url = "${services.profile-service.url}")
public interface ProfileClient {

    @GetMapping("/profiles/{userId}")
    ApiResponse<TenantProfileDTO> getProfileByUserId(@PathVariable Long userId);
    
    @GetMapping("/profiles")
    ApiResponse<List<TenantProfileDTO>> getAllProfiles();
    
    @GetMapping("/profiles/location/{location}")
    ApiResponse<List<TenantProfileDTO>> getProfilesByLocation(@PathVariable String location);
} 
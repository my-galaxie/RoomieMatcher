package com.roomiematcher.profile.repository;

import com.roomiematcher.profile.model.PreferredGender;
import com.roomiematcher.profile.model.TenantProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferredGenderRepository extends JpaRepository<PreferredGender, Long> {
    
    List<PreferredGender> findByTenantProfile(TenantProfile tenantProfile);
    
    void deleteByTenantProfileAndGender(TenantProfile tenantProfile, String gender);
} 
package com.roomiematcher.profile.repository;

import com.roomiematcher.profile.model.TenantProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantProfileRepository extends JpaRepository<TenantProfile, Long> {
    
    Optional<TenantProfile> findByUserId(Long userId);
    
    List<TenantProfile> findByLocation(String location);
    
    boolean existsByUserId(Long userId);
} 
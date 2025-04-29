package com.roomiematcher.repository;

import com.roomiematcher.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    // Custom query methods can be added here if needed
}

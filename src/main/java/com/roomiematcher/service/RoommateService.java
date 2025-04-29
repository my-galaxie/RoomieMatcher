package com.roomiematcher.service;

import com.roomiematcher.model.Tenant;
import com.roomiematcher.repository.TenantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoommateService {

    private final TenantRepository tenantRepository;
    private final MatchingAlgorithm matchingAlgorithm;

    @Autowired
    public RoommateService(TenantRepository tenantRepository, MatchingAlgorithm matchingAlgorithm) {
        this.tenantRepository = tenantRepository;
        this.matchingAlgorithm = matchingAlgorithm;
    }

    /**
     * Computes the compatibility score between two tenants by fetching the latest data
     * from the database and then delegating the calculation to the MatchingAlgorithm.
     *
     * @param tenant1Id the ID of the first tenant
     * @param tenant2Id the ID of the second tenant
     * @return the compatibility score (capped at 100) or 0 if any tenant is not found
     */
    public double computeCompatibility(Long tenant1Id, Long tenant2Id) {
        // Retrieve the tenants from the database
        Tenant tenant1 = tenantRepository.findById(tenant1Id).orElse(null);
        Tenant tenant2 = tenantRepository.findById(tenant2Id).orElse(null);

        if (tenant1 == null || tenant2 == null) {
            // Return 0 if one of the tenants is missing. You could also throw an exception.
            return 0;
        }
        System.out.println("Tenant1: location=" + tenant1.getLocation() +
        ", budget=" + tenant1.getBudget() +
        ", cleanlinessLevel=" + tenant1.getCleanlinessLevel() +
        ", noiseTolerance=" + tenant1.getNoiseTolerance());
System.out.println("Tenant2: location=" + tenant2.getLocation() +
        ", budget=" + tenant2.getBudget() +
        ", cleanlinessLevel=" + tenant2.getCleanlinessLevel() +
        ", noiseTolerance=" + tenant2.getNoiseTolerance());
        // Delegate the matching calculation to the MatchingAlgorithm implementation
        return matchingAlgorithm.calculateMatchScore(tenant1, tenant2);
    }

    /**
     * Optional: Demonstrates a background thread for concurrency.
     * This method starts a daemon thread that runs after the bean is created.
     */
    @PostConstruct
    public void startBackgroundThread() {
        Thread backgroundThread = new Thread(() -> {
            while (true) {
                try {
                    // Sleep for 30 seconds
                    Thread.sleep(30_000);
                    // Log a message or perform background operations (e.g., data cleanup)
                    System.out.println("Background Thread: Checking or cleaning data...");
                } catch (InterruptedException e) {
                    // Restore the interrupt status and break out of the loop
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        // Set as a daemon so it doesn't block JVM shutdown
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }
}

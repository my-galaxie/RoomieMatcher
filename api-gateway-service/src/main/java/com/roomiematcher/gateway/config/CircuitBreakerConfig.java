package com.roomiematcher.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerConfig.class);

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(
            CircuitBreakerRegistry circuitBreakerRegistry,
            TimeLimiterRegistry timeLimiterRegistry) {
        
        return factory -> {
            factory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
            // Configure time limiter registry manually if needed
            
            factory.addCircuitBreakerCustomizer(circuitBreaker -> 
                    circuitBreaker.getEventPublisher()
                        .onStateTransition(event -> 
                            logger.info("Circuit breaker {} state changed from {} to {}", 
                                    event.getCircuitBreakerName(), 
                                    event.getStateTransition().getFromState(), 
                                    event.getStateTransition().getToState())),
                    "auth-service", "profile-service", "match-service", "notification-service");
        };
    }
}

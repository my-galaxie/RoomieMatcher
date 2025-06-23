package com.roomiematcher.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CircuitBreakerConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(
            CircuitBreakerRegistry circuitBreakerRegistry,
            TimeLimiterRegistry timeLimiterRegistry) {
        
        return factory -> {
            factory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
            factory.configureTimeLimiterRegistry(timeLimiterRegistry);
            
            factory.addCircuitBreakerCustomizer(circuitBreaker -> 
                    circuitBreaker.getEventPublisher()
                        .onStateTransition(event -> 
                            log.info("Circuit breaker {} state changed from {} to {}", 
                                    event.getCircuitBreakerName(), 
                                    event.getStateTransition().getFromState(), 
                                    event.getStateTransition().getToState())),
                    "auth-service", "profile-service", "match-service", "notification-service");
        };
    }
}

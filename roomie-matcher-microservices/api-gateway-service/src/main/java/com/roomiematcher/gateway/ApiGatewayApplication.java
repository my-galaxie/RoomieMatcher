package com.roomiematcher.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("${services.auth-service.url}")
                )
                // Profile Service Routes
                .route("profile-service", r -> r
                        .path("/api/profiles/**")
                        .filters(f -> f.rewritePath("/api/profiles/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("${services.profile-service.url}")
                )
                // Match Service Routes
                .route("match-service", r -> r
                        .path("/api/matches/**")
                        .filters(f -> f.rewritePath("/api/matches/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("${services.match-service.url}")
                )
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.rewritePath("/api/notifications/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("${services.notification-service.url}")
                )
                .build();
    }
} 
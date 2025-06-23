package com.roomiematcher.gateway.config;

import com.roomiematcher.gateway.filter.JwtAuthenticationFilter;
import com.roomiematcher.gateway.filter.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RouteConfig {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    @Value("${services.profile-service.url}")
    private String profileServiceUrl;

    @Value("${services.match-service.url}")
    private String matchServiceUrl;

    @Value("${services.notification-service.url}")
    private String notificationServiceUrl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;

    public RouteConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                      RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/auth/(?<segment>.*)", "/api/v1/${segment}")
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config()))
                                .circuitBreaker(c -> c.setName("auth-service")
                                        .setFallbackUri("forward:/fallback/auth"))
                        )
                        .uri(authServiceUrl)
                )
                
                // Profile Service Routes
                .route("profile-service", r -> r
                        .path("/api/profiles/**")
                        .filters(f -> f
                                .rewritePath("/api/profiles/(?<segment>.*)", "/api/v1/${segment}")
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config()))
                                .circuitBreaker(c -> c.setName("profile-service")
                                        .setFallbackUri("forward:/fallback/profile"))
                        )
                        .uri(profileServiceUrl)
                )
                
                // Match Service Routes
                .route("match-service", r -> r
                        .path("/api/matches/**")
                        .filters(f -> f
                                .rewritePath("/api/matches/(?<segment>.*)", "/api/v1/${segment}")
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config()))
                                .circuitBreaker(c -> c.setName("match-service")
                                        .setFallbackUri("forward:/fallback/match"))
                        )
                        .uri(matchServiceUrl)
                )
                
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .rewritePath("/api/notifications/(?<segment>.*)", "/api/v1/${segment}")
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config()))
                                .circuitBreaker(c -> c.setName("notification-service")
                                        .setFallbackUri("forward:/fallback/notification"))
                        )
                        .uri(notificationServiceUrl)
                )
                .build();
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }
} 
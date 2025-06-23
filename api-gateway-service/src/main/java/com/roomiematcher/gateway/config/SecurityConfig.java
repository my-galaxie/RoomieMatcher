package com.roomiematcher.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeExchange()
                    .pathMatchers("/actuator/**").permitAll()
                    .pathMatchers("/api/auth/register", "/api/auth/login", "/api/auth/verify-otp",
                                 "/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
                    .anyExchange().permitAll() // JWT authentication is handled by our custom filter
                .and()
                .build();
    }
} 
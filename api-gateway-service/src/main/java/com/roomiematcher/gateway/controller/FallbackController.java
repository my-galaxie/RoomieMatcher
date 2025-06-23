package com.roomiematcher.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authServiceFallback() {
        log.warn("Fallback triggered for auth-service");
        return createFallbackResponse("Authentication service is currently unavailable. Please try again later.");
    }

    @GetMapping("/profile")
    public Mono<ResponseEntity<Map<String, Object>>> profileServiceFallback() {
        log.warn("Fallback triggered for profile-service");
        return createFallbackResponse("Profile service is currently unavailable. Please try again later.");
    }

    @GetMapping("/match")
    public Mono<ResponseEntity<Map<String, Object>>> matchServiceFallback() {
        log.warn("Fallback triggered for match-service");
        return createFallbackResponse("Match service is currently unavailable. Please try again later.");
    }

    @GetMapping("/notification")
    public Mono<ResponseEntity<Map<String, Object>>> notificationServiceFallback() {
        log.warn("Fallback triggered for notification-service");
        return createFallbackResponse("Notification service is currently unavailable. Please try again later.");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}

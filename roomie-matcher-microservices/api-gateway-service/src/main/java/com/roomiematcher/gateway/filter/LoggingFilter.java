package com.roomiematcher.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        
        ServerHttpRequest request = exchange.getRequest();
        
        // Log the incoming request
        log.info("Request: [{}] {} {} from {}", 
                requestId,
                request.getMethod(),
                request.getURI(),
                request.getRemoteAddress());
        
        // Add the request ID to the request headers
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .build();
        
        // Start the timer
        long startTime = System.currentTimeMillis();
        
        // Continue the filter chain with the modified request
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doFinally(signalType -> {
                    // Calculate the request duration
                    long duration = System.currentTimeMillis() - startTime;
                    
                    // Log the response
                    log.info("Response: [{}] {} completed in {} ms with status {}",
                            requestId,
                            request.getURI(),
                            duration,
                            exchange.getResponse().getStatusCode());
                });
    }

    @Override
    public int getOrder() {
        // Set a high precedence (executed early in the filter chain)
        return -1;
    }
} 
package com.roomiematcher.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        
        ServerHttpRequest request = exchange.getRequest();
        
        // Log the incoming request
        logger.info("Request: [{}] {} {} from {}", 
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
                    logger.info("Response: [{}] {} completed in {} ms with status {}",
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

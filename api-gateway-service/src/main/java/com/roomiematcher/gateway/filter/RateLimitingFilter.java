package com.roomiematcher.gateway.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    // Cache of buckets for each API key or client IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Get the client identifier (IP address or API key)
            String clientId = getClientIdentifier(exchange);
            
            // Get or create a rate limit bucket for this client
            Bucket bucket = buckets.computeIfAbsent(clientId, key -> createBucket(config));
            
            // Try to consume a token from the bucket
            if (bucket.tryConsume(1)) {
                // If successful, continue with the request
                return chain.filter(exchange);
            } else {
                // If rate limit exceeded, return 429 Too Many Requests
                logger.warn("Rate limit exceeded for client: {}", clientId);
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String getClientIdentifier(ServerWebExchange exchange) {
        // Try to get API key from header
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }
        
        // Fall back to client IP address
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    private Bucket createBucket(Config config) {
        // Create a token bucket with the specified capacity and refill rate
        Refill refill = Refill.intervally(config.getRefillTokens(), Duration.ofSeconds(config.getRefillPeriodSeconds()));
        Bandwidth limit = Bandwidth.classic(config.getCapacity(), refill);
        return Bucket4j.builder().addLimit(limit).build();
    }

    public static class Config {
        private int capacity = 20;           // Maximum number of tokens in the bucket
        private int refillTokens = 10;       // Number of tokens to add during each refill
        private int refillPeriodSeconds = 1; // Refill period in seconds
        
        public int getCapacity() {
            return capacity;
        }
        
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
        
        public int getRefillTokens() {
            return refillTokens;
        }
        
        public void setRefillTokens(int refillTokens) {
            this.refillTokens = refillTokens;
        }
        
        public int getRefillPeriodSeconds() {
            return refillPeriodSeconds;
        }
        
        public void setRefillPeriodSeconds(int refillPeriodSeconds) {
            this.refillPeriodSeconds = refillPeriodSeconds;
        }
    }
}

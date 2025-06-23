# API Gateway Service

This service acts as the entry point for all client requests to the RoomieMatcher microservices ecosystem, providing unified routing, security, and resilience features.

## Features

- **Centralized Routing**: Routes requests to the appropriate microservices
- **Authentication**: JWT token validation for secure endpoints
- **Circuit Breaking**: Prevents cascading failures across services
- **Rate Limiting**: Protects services from excessive load
- **Request Logging**: Comprehensive request/response logging
- **CORS Support**: Configurable cross-origin resource sharing
- **Fallback Responses**: Graceful handling of service failures

## API Routes

The API Gateway exposes the following routes:

- `/api/auth/**` → Auth Service
- `/api/profiles/**` → Profile Service
- `/api/matches/**` → Match Service
- `/api/notifications/**` → Notification Service

## Security

- Public endpoints (no authentication required):
  - `/api/auth/register`
  - `/api/auth/login`
  - `/api/auth/verify-otp`
  - `/api/auth/forgot-password`
  - `/api/auth/reset-password`
  - `/actuator/**`

- All other endpoints require a valid JWT token in the Authorization header

## Circuit Breaker Configuration

The gateway implements circuit breakers for all downstream services with the following configuration:

- Sliding window size: 10 requests
- Failure threshold: 50%
- Wait duration in open state: 5 seconds
- Half-open state transition: Automatic
- Timeout duration: 5 seconds

## Rate Limiting

Rate limiting is applied to protect services from excessive load:

- Default: 20 requests per second per IP address
- Token bucket algorithm with 10 refill tokens per second

## Configuration

### Application Properties

```yaml
server:
  port: 8080

services:
  auth-service:
    url: ${AUTH_SERVICE_URL:http://localhost:8081}
  profile-service:
    url: ${PROFILE_SERVICE_URL:http://localhost:8082}
  match-service:
    url: ${MATCH_SERVICE_URL:http://localhost:8083}
  notification-service:
    url: ${NOTIFICATION_SERVICE_URL:http://localhost:8084}
```

### Environment Variables

For production deployment:

- `AUTH_SERVICE_URL` - URL of the Auth Service
- `PROFILE_SERVICE_URL` - URL of the Profile Service
- `MATCH_SERVICE_URL` - URL of the Match Service
- `NOTIFICATION_SERVICE_URL` - URL of the Notification Service
- `JWT_SECRET` - Secret key for JWT validation
- `ALLOWED_ORIGINS` - Comma-separated list of allowed CORS origins

## Docker

The service includes a Dockerfile for containerized deployment. 
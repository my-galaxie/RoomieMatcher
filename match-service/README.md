# Match Service

This microservice is responsible for managing roommate matches and compatibility scoring in the RoomieMatcher application.

## Features

- Calculate compatibility scores between tenant profiles
- Manage match records
- Find potential matches for tenants
- Retrieve match details

## API Endpoints

### Match Management

- `GET /matches/potential/{userId}` - Find potential matches for a user
- `GET /matches/user/{userId}` - Get all matches for a user
- `GET /matches/{matchId}` - Get a specific match by ID

## Architecture

- Uses Spring Boot and Spring Data JPA
- Communicates with the Profile Service using Feign clients
- Stores match data in a PostgreSQL database
- Implements compatibility scoring algorithms

## Matching Algorithm

The service implements a configurable matching algorithm that:

1. Evaluates compatibility based on location, budget, and preferences
2. Handles gender preference matching
3. Scores compatibility on cleanliness, noise tolerance, smoking, and pets
4. Calculates a final match score (0-100)

## Configuration

### Application Properties

```yaml
server:
  port: 8083
  servlet:
    context-path: /api/v1

spring:
  application:
    name: match-service
  datasource:
    url: jdbc:postgresql://localhost:5432/roomie_match
    username: postgres
    password: postgres
```

### Environment Variables

For production deployment:

- `MATCH_DB_URL` - Database URL
- `MATCH_DB_USERNAME` - Database username
- `MATCH_DB_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT validation
- `PROFILE_SERVICE_URL` - URL of the Profile Service

## Docker

The service includes a Dockerfile for containerized deployment:

```dockerfile
FROM maven:3.8.6-openjdk-17-slim AS builder
# ... build steps ...

FROM eclipse-temurin:17.0.5_8-jre-alpine
# ... runtime configuration ...
``` 
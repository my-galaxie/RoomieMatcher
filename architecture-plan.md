# RoomieMatcher Microservices Architecture

## Overview
This document outlines the transformation of the RoomieMatcher monolithic application into a microservices architecture.

## Domain Analysis
Based on the monolithic codebase, we've identified the following core domains:

1. **Authentication & User Management**
2. **Tenant Profile Management**
3. **Roommate Matching**
4. **Notifications**

## Microservices Breakdown

### 1. Auth Service
- **Responsibility**: User registration, authentication, verification, password reset
- **Entities**: User, OtpVerification
- **APIs**: 
  - Register user
  - Login
  - Verify OTP
  - Reset password
  - Refresh token

### 2. Profile Service
- **Responsibility**: Manage tenant profiles and preferences
- **Entities**: Tenant, TenantPreferences
- **APIs**:
  - Create/update profile
  - Get profile
  - Update preferences
  - Search profiles

### 3. Match Service
- **Responsibility**: Implement roommate matching algorithm
- **Entities**: Match
- **APIs**:
  - Generate matches
  - Get matches for a tenant
  - Accept/reject match
  - Get match details

### 4. Notification Service
- **Responsibility**: Send emails and notifications
- **Entities**: Notification, NotificationTemplate
- **APIs**:
  - Send email
  - Send OTP
  - Send match notification
  - Send system notifications

### 5. API Gateway
- **Responsibility**: Route requests, authentication, rate limiting
- **Features**:
  - JWT validation
  - Request routing
  - Rate limiting
  - Circuit breaking
  - Logging

## Common Library
- Shared DTOs
- Exception handling
- Utility classes
- Common security components

## Database Design
Each microservice will have its own database:

1. **Auth DB**: Users, roles, permissions, OTP verifications
2. **Profile DB**: Tenant profiles and preferences
3. **Match DB**: Matches and compatibility scores
4. **Notification DB**: Notification records and templates

## Communication Patterns
- **Synchronous**: REST APIs for direct service-to-service communication
- **Asynchronous**: Future enhancement - Event-driven messaging for notifications and match updates

## Security
- JWT-based authentication
- Service-to-service authentication
- HTTPS for all communications
- Proper secrets management

## Deployment Strategy
- Docker containers for each service
- Docker Compose for local development
- AWS ECS for production deployment
- CI/CD pipeline with AWS CodePipeline

## Monitoring and Observability
- Centralized logging
- Health checks
- Metrics collection
- Distributed tracing (future enhancement) 
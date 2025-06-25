# RoomieMatcher Microservices Architecture

## Overview
This document outlines the architecture of the RoomieMatcher application, which uses a microservices approach deployed on AWS Free Tier resources.

## Domain Analysis
The application is divided into the following core domains:

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
- **Database**: roomie_auth (PostgreSQL)

### 2. Profile Service
- **Responsibility**: Manage tenant profiles and preferences
- **Entities**: TenantProfile, PreferredGender
- **APIs**:
  - Create/update profile
  - Get profile
  - Update preferences
  - Search profiles
- **Database**: roomie_profile (PostgreSQL)
- **Enhanced Features**: Comprehensive tenant preference matching with 14 preference categories

### 3. Match Service
- **Responsibility**: Implement roommate matching algorithm
- **Entities**: Match
- **APIs**:
  - Generate matches
  - Get matches for a tenant
  - Accept/reject match
  - Get match details
- **Database**: roomie_match (PostgreSQL)
- **Algorithm**: Weighted compatibility scoring based on tenant preferences

### 4. Notification Service
- **Responsibility**: Send emails and notifications
- **Entities**: EmailNotification
- **APIs**:
  - Send email
  - Send OTP
  - Send match notification
  - Verify email addresses
- **Database**: roomie_notification (PostgreSQL)
- **Integration**: AWS SES for email delivery

### 5. API Gateway
- **Responsibility**: Route requests, authentication, rate limiting
- **Features**:
  - JWT validation
  - Request routing
  - Rate limiting
  - Circuit breaking
  - Logging
- **Implementation**: Spring Cloud Gateway

## Common Library
- Shared DTOs for cross-service communication
- Exception handling framework
- API response standardization
- Security components

## AWS Deployment Architecture

### Infrastructure Components
1. **AWS Elastic Beanstalk** - Single Instance environment with t2.micro EC2 instance
2. **AWS RDS** - db.t2.micro PostgreSQL instance with multiple databases
3. **AWS SES** - For sending authentication emails and OTP verification
4. **AWS ECR** - For storing Docker images
5. **GitHub Actions** - For CI/CD pipeline

### Deployment Optimizations
1. **Memory Optimization**
   - Containers limited to 128MB memory each
   - JVM heap sizes optimized (96MB max, 48MB min)
   - G1GC garbage collector for better memory management

2. **Database Efficiency**
   - Single RDS instance with multiple databases
   - Connection pooling
   - Optimized Hibernate settings

3. **Resource Constraints**
   - Single instance deployment (no load balancer)
   - Minimal CPU utilization
   - Efficient database queries

## Communication Patterns
- **Synchronous**: REST APIs for direct service-to-service communication
- **Service Discovery**: Direct linking via Docker Compose/Elastic Beanstalk

## Security
- JWT-based authentication
- Service-to-service authentication
- HTTPS for all communications (in production)
- AWS IAM roles for service permissions
- SES email verification for secure communications

## CI/CD Pipeline
- GitHub Actions for automated builds
- Docker image creation and ECR publishing
- Automated deployment to Elastic Beanstalk
- Environment variable management via GitHub Secrets

## Monitoring and Observability
- AWS CloudWatch for logs and metrics
- Health check endpoints
- Custom monitoring script for Free Tier usage
- Application-level logging

## Scalability Considerations
While the current deployment is optimized for AWS Free Tier, the architecture supports scaling:

1. **Horizontal Scaling**
   - Add load balancer and multiple EC2 instances
   - Implement service discovery

2. **Database Scaling**
   - Upgrade to larger RDS instance
   - Enable Multi-AZ for high availability

3. **Enhanced Email Capabilities**
   - Move SES out of sandbox mode
   - Increase sending limits 
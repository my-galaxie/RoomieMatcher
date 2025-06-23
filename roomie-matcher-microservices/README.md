# RoomieMatcher Microservices

A microservices-based application for matching potential roommates based on compatibility and preferences.

## Architecture Overview

RoomieMatcher is built using a microservices architecture with the following components:

1. **Auth Service**: Handles user authentication, registration, and verification
2. **Profile Service**: Manages user profiles and tenant preferences
3. **Match Service**: Implements roommate compatibility algorithms and matching
4. **Notification Service**: Sends email notifications using templates
5. **API Gateway**: Acts as the entry point for all client requests

## Implementation Phases

### Phase 1: Core Infrastructure ✅

- Project structure and common library
- Auth service with JWT authentication
- Profile service with tenant preferences
- Docker configuration for local development

### Phase 2: Matching and Notifications ✅

- Match service with compatibility algorithm
- Notification service with email templates
- Integration between services

### Phase 3: API Gateway and Integration ✅

- API Gateway with Spring Cloud Gateway
- JWT authentication filter
- Circuit breakers and fallbacks
- Rate limiting and request logging
- CORS configuration

### Phase 4: AWS Deployment ✅

- CloudFormation templates for infrastructure as code
- CI/CD pipeline with AWS CodePipeline
- ECS Fargate for container orchestration
- RDS PostgreSQL for databases
- Load balancing and service discovery
- Monitoring and logging with CloudWatch

## Local Development

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker and Docker Compose
- PostgreSQL (or use the Docker Compose setup)

### Running Locally

1. Clone the repository:
```bash
git clone https://github.com/yourusername/roomiematcher.git
cd roomiematcher/roomie-matcher-microservices
```

2. Build all services:
```bash
mvn clean package -DskipTests
```

3. Start the services using Docker Compose:
```bash
docker-compose up -d
```

4. Access the API Gateway at http://localhost:8080

### Service Ports

- API Gateway: 8080
- Auth Service: 8081
- Profile Service: 8082
- Match Service: 8083
- Notification Service: 8084

## AWS Deployment

### Prerequisites

- AWS CLI installed and configured
- GitHub repository with the code
- GitHub OAuth token with repo access
- AWS SES configured with verified domain/email

### Deployment Steps

1. Navigate to the deployment directory:
```bash
cd roomie-matcher-microservices/deployment
```

2. Configure deployment parameters:
```bash
cp scripts/config-example.sh scripts/config-dev.sh
# Edit config-dev.sh with your specific values
```

3. Run the deployment script:
```bash
chmod +x scripts/deploy.sh
./scripts/deploy.sh -e dev -r us-east-1 -o <github-owner> -t <github-token> -j <jwt-secret> -p <db-password> -k <ses-access-key> -s <ses-secret-key>
```

4. Monitor the deployment in the AWS Console:
   - CloudFormation stacks
   - CodePipeline execution
   - ECS services

For detailed deployment instructions, see:
- [AWS Deployment Guide](deployment/AWS_DEPLOYMENT_GUIDE.md)
- [AWS Deployment Quick Start](deployment/AWS_DEPLOYMENT_QUICK_START.md)
- [AWS Deployment Checklist](deployment/aws-deployment-checklist.md)

## API Documentation

API documentation is available via Swagger UI:
- Local: http://localhost:8080/swagger-ui.html
- AWS: http://{load-balancer-dns}/swagger-ui.html

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
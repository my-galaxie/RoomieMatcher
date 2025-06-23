# RoomieMatcher Microservices - AWS Deployment Guide

This guide provides detailed instructions for deploying the RoomieMatcher microservices architecture to AWS using CloudFormation templates and CI/CD pipeline.

## Architecture Overview

The RoomieMatcher microservices architecture consists of the following components:

1. **Auth Service**: Handles user authentication, registration, and verification
2. **Profile Service**: Manages user profiles and tenant preferences
3. **Match Service**: Implements roommate compatibility algorithms and matching
4. **Notification Service**: Sends email notifications using AWS SES
5. **API Gateway**: Acts as the entry point for all client requests

## AWS Infrastructure

The deployment creates the following AWS resources:

- **VPC**: Isolated network with public and private subnets
- **RDS PostgreSQL**: Separate databases for each service
- **ECS Cluster**: Fargate containers for running microservices
- **Service Discovery**: For service-to-service communication
- **Load Balancer**: For routing external traffic to the API Gateway
- **ECR Repositories**: For storing Docker images
- **CI/CD Pipeline**: For automated deployment

## Prerequisites

Before deploying, ensure you have the following:

1. **AWS Account**: With permissions to create all required resources
2. **AWS CLI**: Installed and configured with appropriate credentials
3. **GitHub Repository**: Containing the RoomieMatcher microservices code
4. **GitHub OAuth Token**: With access to the repository
5. **AWS SES**: Configured and verified domain/email for sending emails

## Deployment Steps

### 1. Clone the Repository

```bash
git clone https://github.com/<your-github-username>/roomiematcher.git
cd roomiematcher/roomie-matcher-microservices
```

### 2. Configure Deployment Parameters

Create a configuration file for your deployment environment:

```bash
cp deployment/scripts/config-example.sh deployment/scripts/config-dev.sh
```

Edit the configuration file with your specific parameters:

```bash
vim deployment/scripts/config-dev.sh
```

### 3. Run the Deployment Script

Make the deployment script executable and run it:

```bash
chmod +x deployment/scripts/deploy.sh
./deployment/scripts/deploy.sh -e dev -r us-east-1 -o <github-owner> -t <github-token> -j <jwt-secret> -p <db-password> -k <ses-access-key> -s <ses-secret-key>
```

Replace the placeholders with your actual values:
- `<github-owner>`: Your GitHub username or organization
- `<github-token>`: GitHub OAuth token
- `<jwt-secret>`: Secret for JWT token signing
- `<db-password>`: Database master password
- `<ses-access-key>`: AWS SES access key
- `<ses-secret-key>`: AWS SES secret key

### 4. Monitor Deployment

The deployment script initiates the CI/CD pipeline, which will:

1. Clone the repository from GitHub
2. Build the microservices and Docker images
3. Push the images to ECR repositories
4. Deploy the CloudFormation stacks in sequence:
   - Network infrastructure (VPC, subnets, security groups)
   - RDS PostgreSQL databases
   - ECS cluster and service discovery
   - ECS services and load balancer

You can monitor the deployment progress in the AWS CodePipeline console:
https://console.aws.amazon.com/codepipeline/home?region=<region>#/view/<environment>-roomie-matcher-pipeline

### 5. Access the Application

Once the deployment is complete, you can access the application using the load balancer URL:

```bash
aws cloudformation describe-stacks --stack-name <environment>-roomie-matcher-load-balancer --query "Stacks[0].Outputs[?OutputKey=='LoadBalancerDNS'].OutputValue" --output text
```

## Environment Variables

Each microservice is configured with environment variables for connecting to databases, other services, and external systems:

### Auth Service
- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production deployment
- `SPRING_DATASOURCE_URL`: JDBC URL for the auth database
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: Secret for JWT token signing
- `NOTIFICATION_SERVICE_URL`: URL for the notification service

### Profile Service
- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production deployment
- `SPRING_DATASOURCE_URL`: JDBC URL for the profile database
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: Secret for JWT token signing
- `AUTH_SERVICE_URL`: URL for the auth service
- `NOTIFICATION_SERVICE_URL`: URL for the notification service

### Match Service
- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production deployment
- `SPRING_DATASOURCE_URL`: JDBC URL for the match database
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: Secret for JWT token signing
- `PROFILE_SERVICE_URL`: URL for the profile service
- `NOTIFICATION_SERVICE_URL`: URL for the notification service

### Notification Service
- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production deployment
- `NOTIFICATION_PROVIDER`: Set to `aws-ses` for AWS SES
- `AWS_SES_ACCESS_KEY`: AWS SES access key
- `AWS_SES_SECRET_KEY`: AWS SES secret key
- `AWS_SES_REGION`: AWS SES region
- `AWS_SES_FROM_EMAIL`: Email address to send from

### API Gateway
- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production deployment
- `JWT_SECRET`: Secret for JWT token signing
- `AUTH_SERVICE_URL`: URL for the auth service
- `PROFILE_SERVICE_URL`: URL for the profile service
- `MATCH_SERVICE_URL`: URL for the match service
- `NOTIFICATION_SERVICE_URL`: URL for the notification service
- `ALLOWED_ORIGINS`: Allowed CORS origins

## Scaling

The deployment is configured to automatically scale based on demand:

- **Development Environment**: 1 instance per service
- **Production Environment**: 2 instances per service with auto-scaling

You can modify the scaling configuration in the CloudFormation templates:
- `roomie-matcher-microservices/deployment/cloudformation/services/load-balancer.yaml`

## Monitoring and Logging

All microservices are configured to send logs to CloudWatch Logs:

- **Log Group**: `/ecs/<environment>-roomie-matcher`
- **Log Streams**: `<service-name>/<container-id>`

You can view the logs in the CloudWatch console:
https://console.aws.amazon.com/cloudwatch/home?region=<region>#logsV2:log-groups

## Troubleshooting

### Common Issues

1. **Database Connection Errors**:
   - Check the security group rules to ensure services can access the RDS instances
   - Verify database credentials in AWS Secrets Manager

2. **Service Discovery Issues**:
   - Check the service discovery namespace and service registrations
   - Verify the service URLs in the environment variables

3. **JWT Authentication Errors**:
   - Ensure the JWT secret is correctly set in all services
   - Check the token expiration and validation settings

### Accessing Container Logs

To view logs for a specific service:

```bash
aws logs get-log-events --log-group-name /ecs/<environment>-roomie-matcher --log-stream-name <service-name>/<container-id>
```

## Cleanup

To delete all resources created by the deployment:

```bash
aws cloudformation delete-stack --stack-name <environment>-roomie-matcher-load-balancer
aws cloudformation delete-stack --stack-name <environment>-roomie-matcher-task-definitions
aws cloudformation delete-stack --stack-name <environment>-roomie-matcher-service-discovery
aws cloudformation delete-stack --stack-name <environment>-roomie-matcher-ecs-cluster
aws cloudformation delete-stack --stack-name <environment>-roomie-matcher-rds
aws cloudformation delete-stack --stack-name <environment>-roomie-matcher-vpc
aws cloudformation delete-stack --stack-name <environment>-roomie-matcher-pipeline
```

Also, delete the secrets created in AWS Secrets Manager:

```bash
aws secretsmanager delete-secret --secret-id <environment>-roomie-matcher-jwt --force-delete-without-recovery
aws secretsmanager delete-secret --secret-id <environment>-roomie-matcher-db --force-delete-without-recovery
aws secretsmanager delete-secret --secret-id <environment>-roomie-matcher-ses --force-delete-without-recovery
```

## Security Considerations

1. **Database Credentials**: Stored in AWS Secrets Manager and accessed securely
2. **JWT Secret**: Stored in AWS Secrets Manager and used for token signing
3. **AWS SES Credentials**: Stored in AWS Secrets Manager for secure email sending
4. **Network Security**: Services deployed in private subnets with controlled access
5. **IAM Roles**: Least privilege principle applied to all service roles

## Conclusion

This guide provides a comprehensive approach to deploying the RoomieMatcher microservices architecture on AWS. By following these steps, you can create a scalable, secure, and maintainable infrastructure for your application.

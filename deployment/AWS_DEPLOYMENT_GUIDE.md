# RoomieMatcher AWS Deployment Guide

This guide provides instructions for deploying the RoomieMatcher microservices application to AWS using Amazon ECS (Elastic Container Service).

## Prerequisites

1. AWS Account with appropriate permissions
2. AWS CLI installed and configured
3. Docker installed locally
4. Git repository with the RoomieMatcher codebase

## Architecture Overview

The deployment architecture consists of:

- **Amazon VPC**: Isolated network environment
- **Amazon ECS Cluster**: Container orchestration
- **Amazon RDS**: PostgreSQL database instances
- **Amazon ECR**: Container registry for Docker images
- **Application Load Balancer**: For routing traffic to services
- **AWS Secrets Manager**: For storing sensitive information
- **CloudWatch**: For monitoring and logging

## Step 1: Set Up AWS Resources

### Create a VPC with Public and Private Subnets

```bash
aws cloudformation create-stack \
  --stack-name roomie-matcher-vpc \
  --template-body file://deployment/cloudformation/network/vpc.yaml \
  --parameters ParameterKey=EnvironmentName,ParameterValue=dev
```

### Create RDS Database Instances

```bash
aws cloudformation create-stack \
  --stack-name roomie-matcher-rds \
  --template-body file://deployment/cloudformation/database/rds.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=dev \
    ParameterKey=DBMasterUsername,ParameterValue=admin \
    ParameterKey=DBMasterPassword,ParameterValue=YOUR_SECURE_PASSWORD
```

## Step 2: Set Up Secrets in AWS Secrets Manager

Create secrets for database credentials, JWT tokens, and email service:

```bash
# Database credentials
aws secretsmanager create-secret \
  --name dev-roomie-matcher-db \
  --description "RoomieMatcher database credentials" \
  --secret-string '{"username":"admin","password":"YOUR_SECURE_PASSWORD"}'

# JWT secret
aws secretsmanager create-secret \
  --name dev-roomie-matcher-jwt \
  --description "RoomieMatcher JWT secret" \
  --secret-string '{"secret":"YOUR_JWT_SECRET_KEY"}'

# AWS SES credentials
aws secretsmanager create-secret \
  --name dev-roomie-matcher-ses \
  --description "RoomieMatcher AWS SES credentials" \
  --secret-string '{"accessKey":"YOUR_ACCESS_KEY","secretKey":"YOUR_SECRET_KEY"}'
```

## Step 3: Create ECR Repositories and Push Docker Images

Create ECR repositories for each service:

```bash
aws ecr create-repository --repository-name dev-auth-service
aws ecr create-repository --repository-name dev-profile-service
aws ecr create-repository --repository-name dev-match-service
aws ecr create-repository --repository-name dev-notification-service
aws ecr create-repository --repository-name dev-api-gateway
```

Build and push Docker images:

```bash
# Get ECR login
aws ecr get-login-password --region YOUR_REGION | docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com

# Build and push images
docker build -t YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com/dev-auth-service:latest ./auth-service
docker push YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com/dev-auth-service:latest

# Repeat for other services
```

## Step 4: Create ECS Cluster and Services

### Create ECS Cluster

```bash
aws cloudformation create-stack \
  --stack-name roomie-matcher-ecs-cluster \
  --template-body file://deployment/cloudformation/services/ecs-cluster.yaml \
  --parameters ParameterKey=EnvironmentName,ParameterValue=dev
```

### Create Task Definitions and Services

```bash
aws cloudformation create-stack \
  --stack-name roomie-matcher-task-definitions \
  --template-body file://deployment/cloudformation/services/ecs-task-definitions.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=dev \
    ParameterKey=AuthServiceImage,ParameterValue=YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com/dev-auth-service:latest \
    ParameterKey=ProfileServiceImage,ParameterValue=YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com/dev-profile-service:latest \
    ParameterKey=MatchServiceImage,ParameterValue=YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com/dev-match-service:latest \
    ParameterKey=NotificationServiceImage,ParameterValue=YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com/dev-notification-service:latest \
    ParameterKey=ApiGatewayImage,ParameterValue=YOUR_ACCOUNT_ID.dkr.ecr.YOUR_REGION.amazonaws.com/dev-api-gateway:latest \
    ParameterKey=JwtSecret,ParameterValue='{{resolve:secretsmanager:dev-roomie-matcher-jwt:SecretString:secret}}' \
    ParameterKey=DBMasterUsername,ParameterValue='{{resolve:secretsmanager:dev-roomie-matcher-db:SecretString:username}}' \
    ParameterKey=DBMasterPassword,ParameterValue='{{resolve:secretsmanager:dev-roomie-matcher-db:SecretString:password}}' \
    ParameterKey=AwsSesAccessKey,ParameterValue='{{resolve:secretsmanager:dev-roomie-matcher-ses:SecretString:accessKey}}' \
    ParameterKey=AwsSesSecretKey,ParameterValue='{{resolve:secretsmanager:dev-roomie-matcher-ses:SecretString:secretKey}}' \
    ParameterKey=AwsSesRegion,ParameterValue=YOUR_REGION \
    ParameterKey=AwsSesFromEmail,ParameterValue=no-reply@roomiematcher.com
```

### Create Load Balancer and Service Discovery

```bash
aws cloudformation create-stack \
  --stack-name roomie-matcher-load-balancer \
  --template-body file://deployment/cloudformation/services/load-balancer.yaml \
  --parameters ParameterKey=EnvironmentName,ParameterValue=dev
```

## Step 5: Set Up CI/CD Pipeline (Optional)

Create a CI/CD pipeline using AWS CodePipeline:

```bash
aws cloudformation create-stack \
  --stack-name roomie-matcher-pipeline \
  --template-body file://deployment/cloudformation/pipeline/cicd-pipeline.yaml \
  --parameters \
    ParameterKey=EnvironmentName,ParameterValue=dev \
    ParameterKey=GitHubOwner,ParameterValue=YOUR_GITHUB_USERNAME \
    ParameterKey=GitHubRepo,ParameterValue=roomiematcher \
    ParameterKey=GitHubBranch,ParameterValue=main \
    ParameterKey=GitHubToken,ParameterValue=YOUR_GITHUB_TOKEN \
    ParameterKey=ArtifactBucketName,ParameterValue=roomie-matcher-artifacts
```

## Step 6: Verify Deployment

1. Check the status of your ECS services:

```bash
aws ecs list-services --cluster roomie-matcher-dev
aws ecs describe-services --cluster roomie-matcher-dev --services auth-service profile-service match-service notification-service api-gateway
```

2. Get the Load Balancer DNS name:

```bash
aws elbv2 describe-load-balancers --names roomie-matcher-dev-alb --query 'LoadBalancers[0].DNSName' --output text
```

3. Access the API Gateway through the Load Balancer DNS name:

```
http://<load-balancer-dns-name>/api/v1/health
```

## Cleanup

To remove all resources when they're no longer needed:

```bash
aws cloudformation delete-stack --stack-name roomie-matcher-pipeline
aws cloudformation delete-stack --stack-name roomie-matcher-load-balancer
aws cloudformation delete-stack --stack-name roomie-matcher-task-definitions
aws cloudformation delete-stack --stack-name roomie-matcher-ecs-cluster
aws cloudformation delete-stack --stack-name roomie-matcher-rds
aws cloudformation delete-stack --stack-name roomie-matcher-vpc
```

## Troubleshooting

1. **Service doesn't start**: Check CloudWatch logs for errors
2. **Database connection issues**: Verify security groups and network ACLs
3. **Load balancer health check failures**: Ensure services are running and health check paths are correct
4. **Image pull failures**: Verify ECR repository permissions and image tags

## Security Considerations

1. Use IAM roles with least privilege
2. Store sensitive information in AWS Secrets Manager
3. Use VPC endpoints for AWS services
4. Enable encryption for data at rest and in transit
5. Implement proper network security with security groups and NACLs

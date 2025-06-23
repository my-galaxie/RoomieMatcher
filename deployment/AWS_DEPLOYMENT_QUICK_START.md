# RoomieMatcher Microservices - AWS Deployment Quick Start

This quick start guide provides the essential steps to deploy the RoomieMatcher microservices to AWS using CloudFormation templates and CI/CD pipeline.

## Prerequisites

1. **AWS CLI** installed and configured
2. **GitHub** repository with RoomieMatcher code
3. **GitHub OAuth Token** with repo access
4. **AWS SES** configured with verified domain/email

## Quick Deployment Steps

### 1. Clone the Repository

```bash
git clone https://github.com/<your-github-username>/roomiematcher.git
cd roomiematcher/roomie-matcher-microservices
```

### 2. Make the Deployment Script Executable

```bash
chmod +x deployment/scripts/deploy.sh
```

### 3. Run the Deployment Script

```bash
./deployment/scripts/deploy.sh \
  -e dev \
  -r us-east-1 \
  -o <github-owner> \
  -t <github-token> \
  -j <jwt-secret> \
  -p <db-password> \
  -k <ses-access-key> \
  -s <ses-secret-key> \
  -f no-reply@yourdomain.com
```

Replace the placeholders with your actual values:
- `<github-owner>`: Your GitHub username or organization
- `<github-token>`: GitHub OAuth token
- `<jwt-secret>`: Secret for JWT token signing (generate a strong random string)
- `<db-password>`: Database master password
- `<ses-access-key>`: AWS SES access key
- `<ses-secret-key>`: AWS SES secret key

### 4. Monitor Deployment Progress

The deployment creates the following CloudFormation stacks:
- `dev-roomie-matcher-pipeline`
- `dev-roomie-matcher-vpc`
- `dev-roomie-matcher-rds`
- `dev-roomie-matcher-ecs-cluster`
- `dev-roomie-matcher-service-discovery`
- `dev-roomie-matcher-task-definitions`
- `dev-roomie-matcher-load-balancer`

Monitor the progress in the AWS Console:
https://console.aws.amazon.com/cloudformation

### 5. Access the Application

Once the deployment is complete, get the load balancer URL:

```bash
aws cloudformation describe-stacks \
  --stack-name dev-roomie-matcher-load-balancer \
  --query "Stacks[0].Outputs[?OutputKey=='LoadBalancerDNS'].OutputValue" \
  --output text
```

## Deployment Architecture

```
                                    ┌───────────────┐
                                    │               │
                                    │  API Gateway  │
                                    │   (8080)      │
                                    │               │
                                    └───────┬───────┘
                                            │
                    ┌───────────────────────┼───────────────────────┐
                    │                       │                       │
           ┌────────▼─────────┐   ┌─────────▼──────────┐   ┌────────▼─────────┐
           │                  │   │                    │   │                  │
           │   Auth Service   │   │  Profile Service   │   │   Match Service  │
           │     (8081)       │   │      (8082)        │   │     (8083)       │
           │                  │   │                    │   │                  │
           └────────┬─────────┘   └─────────┬──────────┘   └────────┬─────────┘
                    │                       │                       │
                    └───────────────────────┼───────────────────────┘
                                            │
                                    ┌───────▼───────┐
                                    │               │
                                    │ Notification  │
                                    │   Service     │
                                    │    (8084)     │
                                    │               │
                                    └───────────────┘
```

## Common Issues and Solutions

1. **Pipeline Fails at Source Stage**:
   - Check GitHub token permissions
   - Verify repository exists and is accessible

2. **Build Stage Fails**:
   - Check Maven build errors in CodeBuild logs
   - Verify Docker build commands

3. **Deployment Stages Fail**:
   - Check CloudFormation template errors
   - Verify parameter values are correct

4. **Services Not Communicating**:
   - Check service discovery configuration
   - Verify security group rules

5. **Email Notifications Not Working**:
   - Verify SES credentials
   - Check if email/domain is verified in SES

## Cleanup

To delete all resources:

```bash
./deployment/scripts/cleanup.sh -e dev -r us-east-1
```

For more detailed information, refer to the comprehensive [AWS Deployment Guide](AWS_DEPLOYMENT_GUIDE.md). 
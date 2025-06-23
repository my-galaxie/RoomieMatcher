# RoomieMatcher AWS Elastic Beanstalk Deployment Guide

This guide provides step-by-step instructions for deploying the RoomieMatcher microservices application on AWS Elastic Beanstalk.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Deployment Steps](#deployment-steps)
4. [Cost Estimation](#cost-estimation)
5. [Time Estimation](#time-estimation)
6. [Monitoring and Maintenance](#monitoring-and-maintenance)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

Before you begin, ensure you have the following:

- AWS Account with appropriate permissions
- AWS CLI installed and configured
- Docker installed locally
- Java 17 or later installed
- Maven installed
- Git installed

## Project Structure

The RoomieMatcher application consists of the following microservices:

- **API Gateway Service**: Routes requests to appropriate microservices
- **Auth Service**: Handles user authentication and authorization
- **Profile Service**: Manages user profiles
- **Match Service**: Implements roommate matching algorithm
- **Notification Service**: Sends email notifications

The project has been configured for Elastic Beanstalk deployment with:

- `Dockerrun.aws.json`: Multi-container Docker configuration
- `.ebextensions/`: Configuration files for the Elastic Beanstalk environment
- `buildspec.yml`: AWS CodeBuild configuration

## Deployment Steps

### Step 1: Set Up AWS Infrastructure

1. **Create an S3 Bucket for Deployment Artifacts**

```bash
aws s3 mb s3://roomiematcher-deployment-artifacts --region <your-region>
```

2. **Create ECR Repositories for Docker Images**

```bash
aws ecr create-repository --repository-name roomiematcher-api-gateway --region <your-region>
aws ecr create-repository --repository-name roomiematcher-auth-service --region <your-region>
aws ecr create-repository --repository-name roomiematcher-profile-service --region <your-region>
aws ecr create-repository --repository-name roomiematcher-match-service --region <your-region>
aws ecr create-repository --repository-name roomiematcher-notification-service --region <your-region>
```

3. **Create an RDS Instance for PostgreSQL** (Optional if using embedded PostgreSQL)

```bash
aws rds create-db-instance \
    --db-instance-identifier roomiematcher-db \
    --db-instance-class db.t3.micro \
    --engine postgres \
    --allocated-storage 20 \
    --master-username postgres \
    --master-user-password <your-password> \
    --vpc-security-group-ids <security-group-id> \
    --db-subnet-group-name <subnet-group-name> \
    --region <your-region>
```

### Step 2: Build and Push Docker Images

1. **Log in to ECR**

```bash
aws ecr get-login-password --region <your-region> | docker login --username AWS --password-stdin <your-account-id>.dkr.ecr.<your-region>.amazonaws.com
```

2. **Build Docker Images**

```bash
mvn clean install
docker build -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-api-gateway:latest ./api-gateway-service
docker build -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-auth-service:latest ./auth-service
docker build -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-profile-service:latest ./profile-service
docker build -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-match-service:latest ./match-service
docker build -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-notification-service:latest ./notification-service
```

3. **Push Docker Images to ECR**

```bash
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-api-gateway:latest
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-auth-service:latest
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-profile-service:latest
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-match-service:latest
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-notification-service:latest
```

### Step 3: Create Elastic Beanstalk Application and Environment

1. **Create the Elastic Beanstalk Application**

```bash
aws elasticbeanstalk create-application --application-name roomiematcher --region <your-region>
```

2. **Create an Application Version**

```bash
# First, update Dockerrun.aws.json with your account ID and region
sed -i "s|\${AWS_ACCOUNT_ID}|<your-account-id>|g" Dockerrun.aws.json
sed -i "s|\${AWS_REGION}|<your-region>|g" Dockerrun.aws.json

# Create a ZIP file with deployment files
zip -r deployment.zip Dockerrun.aws.json .ebextensions/

# Upload to S3
aws s3 cp deployment.zip s3://roomiematcher-deployment-artifacts/

# Create application version
aws elasticbeanstalk create-application-version \
    --application-name roomiematcher \
    --version-label v1 \
    --source-bundle S3Bucket=roomiematcher-deployment-artifacts,S3Key=deployment.zip \
    --region <your-region>
```

3. **Create the Environment**

```bash
aws elasticbeanstalk create-environment \
    --application-name roomiematcher \
    --environment-name roomiematcher-env \
    --solution-stack-name "64bit Amazon Linux 2 v3.5.3 running Docker" \
    --version-label v1 \
    --option-settings file://env-config.json \
    --region <your-region>
```

Create a file named `env-config.json` with the following content:

```json
[
  {
    "Namespace": "aws:elasticbeanstalk:application:environment",
    "OptionName": "SPRING_PROFILES_ACTIVE",
    "Value": "prod"
  },
  {
    "Namespace": "aws:elasticbeanstalk:application:environment",
    "OptionName": "JWT_SECRET",
    "Value": "your-jwt-secret-here"
  },
  {
    "Namespace": "aws:autoscaling:launchconfiguration",
    "OptionName": "InstanceType",
    "Value": "t2.small"
  }
]
```

### Step 4: Set Up CI/CD Pipeline (Optional)

1. **Create a CodeBuild Project**

```bash
aws codebuild create-project \
    --name roomiematcher-build \
    --source type=GITHUB,location=https://github.com/yourusername/roomiematcher.git \
    --artifacts type=S3,location=roomiematcher-deployment-artifacts,path=builds,name=roomiematcher.zip \
    --environment type=LINUX_CONTAINER,image=aws/codebuild/amazonlinux2-x86_64-standard:3.0,computeType=BUILD_GENERAL1_SMALL,privilegedMode=true \
    --service-role codebuild-service-role \
    --environment-variables name=AWS_DEFAULT_REGION,value=<your-region>,type=PLAINTEXT name=AWS_ACCOUNT_ID,value=<your-account-id>,type=PLAINTEXT \
    --region <your-region>
```

2. **Create a CodePipeline**

```bash
aws codepipeline create-pipeline \
    --pipeline-name roomiematcher-pipeline \
    --role-arn arn:aws:iam::<your-account-id>:role/codepipeline-service-role \
    --artifact-store type=S3,location=roomiematcher-deployment-artifacts \
    --pipeline file://pipeline-definition.json \
    --region <your-region>
```

Create a file named `pipeline-definition.json` with the appropriate pipeline configuration.

### Step 5: Configure Database

1. **If using RDS**:
   - Update the application properties in each service to point to the RDS instance
   - Run the database migration scripts

2. **If using embedded PostgreSQL**:
   - The database will be created automatically when the application starts

### Step 6: Deploy and Test

1. **Monitor the Deployment**

```bash
aws elasticbeanstalk describe-environments --environment-names roomiematcher-env --region <your-region>
```

2. **Access the Application**

Once the deployment is complete, you can access your application at:
```
http://roomiematcher-env.xxxxxxxx.region.elasticbeanstalk.com
```

## Cost Estimation

The estimated monthly cost for running the RoomieMatcher application on AWS Elastic Beanstalk:

| Resource | Specification | Monthly Cost (USD) |
|----------|---------------|-------------------|
| EC2 Instance | t2.small (2 GB RAM, 1 vCPU) | $16.80 |
| Load Balancer | Application Load Balancer | $16.20 |
| RDS Instance | db.t3.micro (1 GB RAM) | $12.24 |
| S3 Storage | 5 GB | $0.12 |
| ECR Storage | 1 GB | $0.10 |
| Data Transfer | 50 GB | $4.50 |
| **Total** | | **$49.96** |

**Cost Optimization Options**:
- Use AWS Free Tier resources where possible
- Scale down during non-peak hours
- Use spot instances for non-critical components
- Consider using AWS RDS Aurora Serverless for cost-effective database scaling

## Time Estimation

| Task | Estimated Time |
|------|---------------|
| AWS Account Setup and Configuration | 1-2 hours |
| Building and Pushing Docker Images | 1-2 hours |
| Creating Elastic Beanstalk Environment | 1-2 hours |
| Database Setup and Configuration | 1-2 hours |
| Testing and Troubleshooting | 2-4 hours |
| **Total** | **6-12 hours** |

Note: This estimation assumes familiarity with AWS services and Docker. Additional time may be needed for learning and troubleshooting if you're new to these technologies.

## Monitoring and Maintenance

### CloudWatch Monitoring

1. **Set Up CloudWatch Alarms**

```bash
aws cloudwatch put-metric-alarm \
    --alarm-name roomiematcher-cpu-high \
    --alarm-description "Alarm when CPU exceeds 70%" \
    --metric-name CPUUtilization \
    --namespace AWS/EC2 \
    --statistic Average \
    --period 300 \
    --threshold 70 \
    --comparison-operator GreaterThanThreshold \
    --dimensions Name=AutoScalingGroupName,Value=<your-asg-name> \
    --evaluation-periods 2 \
    --alarm-actions <your-sns-topic-arn> \
    --region <your-region>
```

2. **Enable Enhanced Health Reporting**

This is already configured in the `.ebextensions/01_environment.config` file.

### Logging

- Application logs are available in CloudWatch Logs
- You can access the logs through the Elastic Beanstalk console or using the AWS CLI:

```bash
aws logs get-log-events \
    --log-group-name /aws/elasticbeanstalk/roomiematcher-env/var/log/app/application.log \
    --log-stream-name <log-stream-name> \
    --region <your-region>
```

### Updating the Application

1. **Build and Push New Docker Images**

```bash
docker build -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-api-gateway:v2 ./api-gateway-service
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-api-gateway:v2
```

2. **Update Dockerrun.aws.json with the New Image Tags**

3. **Create a New Application Version and Deploy**

```bash
zip -r deployment-v2.zip Dockerrun.aws.json .ebextensions/
aws s3 cp deployment-v2.zip s3://roomiematcher-deployment-artifacts/
aws elasticbeanstalk create-application-version \
    --application-name roomiematcher \
    --version-label v2 \
    --source-bundle S3Bucket=roomiematcher-deployment-artifacts,S3Key=deployment-v2.zip \
    --region <your-region>
aws elasticbeanstalk update-environment \
    --environment-name roomiematcher-env \
    --version-label v2 \
    --region <your-region>
```

## Troubleshooting

### Common Issues and Solutions

1. **Connection Issues Between Services**
   - Check security groups and ensure they allow traffic between containers
   - Verify service URLs in the environment variables

2. **Database Connection Issues**
   - Check database credentials in environment variables
   - Ensure the RDS security group allows connections from the Elastic Beanstalk environment

3. **Deployment Failures**
   - Check Elastic Beanstalk logs for errors
   - Verify Docker images are correctly built and pushed to ECR
   - Check for syntax errors in Dockerrun.aws.json or .ebextensions files

4. **Application Errors**
   - Check application logs in CloudWatch
   - SSH into the EC2 instance for direct troubleshooting:

```bash
aws elasticbeanstalk retrieve-environment-info \
    --environment-name roomiematcher-env \
    --info-type tail \
    --region <your-region>
```

### Support Resources

- [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/Welcome.html)
- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

For additional support, contact your AWS account manager or open a support ticket through the AWS Management Console. 
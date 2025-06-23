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
- PostgreSQL client (psql) installed

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

3. **Create an RDS Instance for PostgreSQL**

```bash
# Deploy the CloudFormation template for RDS
aws cloudformation create-stack \
  --stack-name roomiematcher-db \
  --template-body file://deployment/cloudformation/database/rds.yaml \
  --parameters \
    ParameterKey=VpcId,ParameterValue=<your-vpc-id> \
    ParameterKey=SubnetIds,ParameterValue="<subnet-id-1>,<subnet-id-2>" \
    ParameterKey=DBUsername,ParameterValue=<master-username> \
    ParameterKey=DBPassword,ParameterValue=<master-password> \
    ParameterKey=Environment,ParameterValue=prod \
  --region <your-region>
```

4. **Get the RDS Endpoint**

```bash
aws cloudformation describe-stacks \
  --stack-name roomiematcher-db \
  --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" \
  --output text \
  --region <your-region>
```

5. **Initialize the Databases**

```bash
# Run the database initialization script
cd deployment/scripts
./setup-rds-databases.sh <rds-endpoint> <master-username> <master-password>
```

6. **Generate Environment Variables**

```bash
# Generate environment variables for your services
./setup-env-variables.sh <rds-endpoint> env-variables.txt
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
# Update Dockerrun.aws.json with your account ID, region, and other environment variables
# Use the values from the env-variables.txt file generated earlier
source env-variables.txt

# Replace placeholders in Dockerrun.aws.json
sed -i "s|\${AWS_ACCOUNT_ID}|<your-account-id>|g" Dockerrun.aws.json
sed -i "s|\${AWS_REGION}|<your-region>|g" Dockerrun.aws.json
sed -i "s|\${JWT_SECRET}|$JWT_SECRET|g" Dockerrun.aws.json
sed -i "s|\${RDS_ENDPOINT}|$RDS_ENDPOINT|g" Dockerrun.aws.json
sed -i "s|\${MASTER_DB_USERNAME}|$MASTER_DB_USERNAME|g" Dockerrun.aws.json
sed -i "s|\${MASTER_DB_PASSWORD}|$MASTER_DB_PASSWORD|g" Dockerrun.aws.json
sed -i "s|\${AUTH_DB_USERNAME}|$AUTH_DB_USERNAME|g" Dockerrun.aws.json
sed -i "s|\${AUTH_DB_PASSWORD}|$AUTH_DB_PASSWORD|g" Dockerrun.aws.json
sed -i "s|\${PROFILE_DB_USERNAME}|$PROFILE_DB_USERNAME|g" Dockerrun.aws.json
sed -i "s|\${PROFILE_DB_PASSWORD}|$PROFILE_DB_PASSWORD|g" Dockerrun.aws.json
sed -i "s|\${MATCH_DB_USERNAME}|$MATCH_DB_USERNAME|g" Dockerrun.aws.json
sed -i "s|\${MATCH_DB_PASSWORD}|$MATCH_DB_PASSWORD|g" Dockerrun.aws.json
sed -i "s|\${NOTIFICATION_DB_USERNAME}|$NOTIFICATION_DB_USERNAME|g" Dockerrun.aws.json
sed -i "s|\${NOTIFICATION_DB_PASSWORD}|$NOTIFICATION_DB_PASSWORD|g" Dockerrun.aws.json

# Create a ZIP file with deployment files
mkdir -p deploy_package
cp -r .ebextensions deploy_package/
cp Dockerrun.aws.json deploy_package/
cd deploy_package && zip -r ../deploy.zip .

# Upload to S3
aws s3 cp deploy.zip s3://roomiematcher-deployment-artifacts/

# Create application version
aws elasticbeanstalk create-application-version \
    --application-name roomiematcher \
    --version-label v1 \
    --source-bundle S3Bucket=roomiematcher-deployment-artifacts,S3Key=deploy.zip \
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
    "Namespace": "aws:autoscaling:launchconfiguration",
    "OptionName": "InstanceType",
    "Value": "t2.small"
  }
]
```

### Step 4: Set Up CI/CD Pipeline (Optional)

1. **Create a GitHub Actions Workflow**

The project already includes a GitHub Actions workflow file (`.github/workflows/deploy-to-aws.yml`) that automates the deployment process. To use it:

- Add the required secrets to your GitHub repository (see DEPLOYMENT_SECRETS.md)
- Push your code to the main branch to trigger the workflow

2. **Alternative: Create a CodeBuild Project**

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

### Step 5: Deploy and Test

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
- Use a single RDS instance with multiple databases (already implemented)

## Time Estimation

| Task | Estimated Time |
|------|---------------|
| AWS Account Setup and Configuration | 1-2 hours |
| RDS Setup and Database Initialization | 1-2 hours |
| Building and Pushing Docker Images | 1-2 hours |
| Creating Elastic Beanstalk Environment | 1-2 hours |
| Testing and Troubleshooting | 2-4 hours |
| **Total** | **6-12 hours** |

## Monitoring and Maintenance

1. **CloudWatch Monitoring**

Set up CloudWatch alarms for:
- EC2 instance CPU and memory usage
- RDS instance CPU, memory, and storage usage
- Application load balancer request count and latency

2. **Database Maintenance**

- Schedule regular backups of your RDS instance
- Monitor database performance and optimize queries as needed
- Periodically check for database updates and security patches

3. **Application Logs**

- Configure your services to send logs to CloudWatch Logs
- Set up log retention policies to manage storage costs
- Create CloudWatch Dashboards to visualize application metrics

## Troubleshooting

1. **Deployment Issues**

- Check Elastic Beanstalk events and logs in the AWS Management Console
- Verify that all environment variables are correctly set
- Ensure that security groups allow traffic between services

2. **Database Connection Issues**

- Verify that the RDS security group allows traffic from the Elastic Beanstalk environment
- Check that database credentials are correct in environment variables
- Ensure that the database initialization script ran successfully

3. **Service Communication Issues**

- Check that service URLs are correctly configured
- Verify that all services are running and healthy
- Inspect network traffic between services using AWS VPC Flow Logs

For additional support, contact your AWS account manager or open a support ticket through the AWS Management Console. 
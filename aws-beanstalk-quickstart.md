# RoomieMatcher AWS Elastic Beanstalk Quick Start Guide

This guide provides a quick way to deploy the RoomieMatcher application on AWS Elastic Beanstalk.

## Prerequisites

- AWS Account
- AWS CLI installed and configured
- Docker installed
- Git repository with the RoomieMatcher code
- PostgreSQL client (psql) installed

## Deployment Steps

### 1. Clone the Repository

```bash
git clone <repository-url>
cd roomiematcher
```

### 2. Build the Application

```bash
mvn clean install
```

### 3. Create RDS Instance and Initialize Databases

```bash
# Set your AWS region
export AWS_REGION=us-east-1

# Create RDS instance using CloudFormation
aws cloudformation create-stack \
  --stack-name roomiematcher-db \
  --template-body file://deployment/cloudformation/database/rds.yaml \
  --parameters \
    ParameterKey=VpcId,ParameterValue=<your-vpc-id> \
    ParameterKey=SubnetIds,ParameterValue="<subnet-id-1>,<subnet-id-2>" \
    ParameterKey=DBUsername,ParameterValue=postgres \
    ParameterKey=DBPassword,ParameterValue=<secure-password> \
    ParameterKey=Environment,ParameterValue=prod \
  --region $AWS_REGION

# Wait for stack creation to complete
aws cloudformation wait stack-create-complete --stack-name roomiematcher-db --region $AWS_REGION

# Get RDS endpoint
export RDS_ENDPOINT=$(aws cloudformation describe-stacks \
  --stack-name roomiematcher-db \
  --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" \
  --output text \
  --region $AWS_REGION)

# Initialize databases
cd deployment/scripts
chmod +x setup-rds-databases.sh
./setup-rds-databases.sh $RDS_ENDPOINT postgres <secure-password>

# Generate environment variables
chmod +x setup-env-variables.sh
./setup-env-variables.sh $RDS_ENDPOINT env-variables.txt
cd ../..

# Source the environment variables
source deployment/scripts/env-variables.txt
```

### 4. Create ECR Repositories

```bash
# Get your AWS account ID
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# Create repositories
aws ecr create-repository --repository-name roomiematcher-api-gateway --region $AWS_REGION
aws ecr create-repository --repository-name roomiematcher-auth-service --region $AWS_REGION
aws ecr create-repository --repository-name roomiematcher-profile-service --region $AWS_REGION
aws ecr create-repository --repository-name roomiematcher-match-service --region $AWS_REGION
aws ecr create-repository --repository-name roomiematcher-notification-service --region $AWS_REGION
```

### 5. Build and Push Docker Images

```bash
# Login to ECR
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# Build and push images
docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-api-gateway:latest ./api-gateway-service
docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-auth-service:latest ./auth-service
docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-profile-service:latest ./profile-service
docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-match-service:latest ./match-service
docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-notification-service:latest ./notification-service

docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-api-gateway:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-auth-service:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-profile-service:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-match-service:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/roomiematcher-notification-service:latest
```

### 6. Update Dockerrun.aws.json

```bash
# Replace placeholders in Dockerrun.aws.json
sed -i "s|\${AWS_ACCOUNT_ID}|$AWS_ACCOUNT_ID|g" Dockerrun.aws.json
sed -i "s|\${AWS_REGION}|$AWS_REGION|g" Dockerrun.aws.json
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
```

### 7. Create an S3 Bucket for Deployment

```bash
aws s3 mb s3://roomiematcher-deployment-$AWS_ACCOUNT_ID --region $AWS_REGION
```

### 8. Create a Deployment Package

```bash
mkdir -p deploy_package
cp -r .ebextensions deploy_package/
cp Dockerrun.aws.json deploy_package/
cd deploy_package && zip -r ../deploy.zip .
cd ..
```

### 9. Upload the Deployment Package to S3

```bash
aws s3 cp deploy.zip s3://roomiematcher-deployment-$AWS_ACCOUNT_ID/
```

### 10. Create an Elastic Beanstalk Application

```bash
aws elasticbeanstalk create-application --application-name roomiematcher --region $AWS_REGION
```

### 11. Create an Application Version

```bash
aws elasticbeanstalk create-application-version \
    --application-name roomiematcher \
    --version-label v1 \
    --source-bundle S3Bucket=roomiematcher-deployment-$AWS_ACCOUNT_ID,S3Key=deploy.zip \
    --region $AWS_REGION
```

### 12. Create an Environment

For minimal cost (development):

```bash
aws elasticbeanstalk create-environment \
    --application-name roomiematcher \
    --environment-name roomiematcher-dev \
    --solution-stack-name "64bit Amazon Linux 2 v3.5.3 running Docker" \
    --version-label v1 \
    --option-settings file://env-config-minimal.json \
    --region $AWS_REGION
```

For production:

```bash
aws elasticbeanstalk create-environment \
    --application-name roomiematcher \
    --environment-name roomiematcher-prod \
    --solution-stack-name "64bit Amazon Linux 2 v3.5.3 running Docker" \
    --version-label v1 \
    --option-settings file://env-config-optimized.json \
    --region $AWS_REGION
```

### 13. Monitor Deployment

```bash
aws elasticbeanstalk describe-environments \
    --environment-names roomiematcher-dev \
    --region $AWS_REGION
```

### 14. Access Your Application

Once the environment status is "Ready", you can access your application at:

```
http://roomiematcher-dev.xxxxxxxx.region.elasticbeanstalk.com
```

## Troubleshooting

1. **Check Environment Health**:

```bash
aws elasticbeanstalk describe-environment-health \
    --environment-name roomiematcher-dev \
    --attribute-names All \
    --region $AWS_REGION
```

2. **View Logs**:

```bash
aws elasticbeanstalk request-environment-info \
    --environment-name roomiematcher-dev \
    --info-type tail \
    --region $AWS_REGION
```

3. **Check Database Connectivity**:

```bash
# Connect to the RDS instance
psql -h $RDS_ENDPOINT -U postgres -d postgres

# List databases
\l

# Connect to a specific database
\c auth_db

# List tables
\dt
```

## Clean Up Resources

```bash
# Terminate the environment
aws elasticbeanstalk terminate-environment \
    --environment-name roomiematcher-dev \
    --region $AWS_REGION

# Delete the application
aws elasticbeanstalk delete-application \
    --application-name roomiematcher \
    --terminate-env-by-force \
    --region $AWS_REGION

# Delete RDS instance (via CloudFormation stack)
aws cloudformation delete-stack --stack-name roomiematcher-db --region $AWS_REGION

# Delete ECR repositories
aws ecr delete-repository --repository-name roomiematcher-api-gateway --force --region $AWS_REGION
aws ecr delete-repository --repository-name roomiematcher-auth-service --force --region $AWS_REGION
aws ecr delete-repository --repository-name roomiematcher-profile-service --force --region $AWS_REGION
aws ecr delete-repository --repository-name roomiematcher-match-service --force --region $AWS_REGION
aws ecr delete-repository --repository-name roomiematcher-notification-service --force --region $AWS_REGION

# Delete S3 bucket
aws s3 rb s3://roomiematcher-deployment-$AWS_ACCOUNT_ID --force --region $AWS_REGION
```

For more detailed information, refer to the comprehensive [AWS Deployment Guide](aws-beanstalk-deployment-guide.md). 
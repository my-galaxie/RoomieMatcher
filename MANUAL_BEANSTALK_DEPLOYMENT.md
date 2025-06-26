# Manual AWS Elastic Beanstalk Deployment Guide

This guide provides step-by-step instructions for manually deploying the RoomieMatcher application to AWS Elastic Beanstalk.

## Prerequisites

- AWS Account with access to Elastic Beanstalk, ECR, RDS, and IAM
- AWS CLI installed and configured
- Docker installed locally
- Maven installed locally
- Git repository with RoomieMatcher code

## Step 1: Set Up AWS Resources

### 1.1 Create RDS Database

1. Log in to the AWS Management Console
2. Navigate to RDS service
3. Click "Create database"
4. Select "Standard create" and choose "PostgreSQL"
5. Under "Templates" select "Free tier"
6. Configure the following:
   - DB instance identifier: `roomiematcher-db`
   - Master username: `postgres`
   - Master password: (create a secure password)
7. Under "Connectivity" select your VPC and create a new security group
8. Under "Additional configuration" set:
   - Initial database name: `postgres`
   - Enable automated backups: Yes
   - Backup retention period: 7 days
9. Click "Create database"

### 1.2 Configure Security Group for RDS

1. Navigate to EC2 → Security Groups
2. Find the security group created for your RDS instance
3. Add an inbound rule:
   - Type: PostgreSQL
   - Source: Your IP address
4. Save rules

### 1.3 Initialize Databases

1. Connect to the RDS instance using a PostgreSQL client
2. Run the following commands to create the required databases:

```sql
CREATE DATABASE roomie_auth;
CREATE DATABASE roomie_profile;
CREATE DATABASE roomie_match;
CREATE DATABASE roomie_notification;
```

3. Initialize each database schema using the SQL files in the repository:
   - auth-service/src/main/resources/db/migration/V1__init_schema.sql
   - profile-service/src/main/resources/db/migration/V1__init_schema.sql
   - profile-service/src/main/resources/db/migration/V2__add_tenant_preferences.sql
   - match-service/src/main/resources/db/migration/V1__init_schema.sql

### 1.4 Configure AWS SES

1. Navigate to Amazon SES
2. Go to "Email Addresses" under "Identity Management"
3. Click "Verify a New Email Address"
4. Enter your email address and verify it
5. Create IAM user with SES permissions:
   - Navigate to IAM
   - Create a new user with programmatic access
   - Attach the "AmazonSESFullAccess" policy
   - Save the access key and secret key

## Step 2: Build and Package the Application

### 2.1 Build the Application

```bash
# Clone the repository if you haven't already
git clone <repository-url>
cd roomiematcher

# Build the application
mvn clean package -DskipTests
```

### 2.2 Create Amazon ECR Repositories

```bash
# Create repositories for each service
aws ecr create-repository --repository-name roomiematcher-api-gateway
aws ecr create-repository --repository-name roomiematcher-auth-service
aws ecr create-repository --repository-name roomiematcher-profile-service
aws ecr create-repository --repository-name roomiematcher-match-service
aws ecr create-repository --repository-name roomiematcher-notification-service
```

### 2.3 Build and Push Docker Images

```bash
# Log in to ECR
aws ecr get-login-password --region <your-region> | docker login --username AWS --password-stdin <your-account-id>.dkr.ecr.<your-region>.amazonaws.com

# Build and push each service
for service in api-gateway-service auth-service profile-service match-service notification-service; do
  service_name=$(echo $service | sed 's/-service//')
  docker build -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-$service_name:latest ./$service
  docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-$service_name:latest
done
```

### 2.4 Create Dockerrun.aws.json

Create a file named `Dockerrun.aws.json` with the following content, replacing placeholders with your actual values:

```json
{
  "AWSEBDockerrunVersion": "2",
  "containerDefinitions": [
    {
      "name": "api-gateway",
      "image": "<your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-api-gateway:latest",
      "essential": true,
      "memory": 128,
      "portMappings": [
        {
          "hostPort": 80,
          "containerPort": 8080
        }
      ],
      "links": [
        "auth-service",
        "profile-service",
        "match-service",
        "notification-service"
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "JWT_SECRET",
          "value": "<your-jwt-secret>"
        },
        {
          "name": "AUTH_SERVICE_URL",
          "value": "http://auth-service:8081/api/v1"
        },
        {
          "name": "PROFILE_SERVICE_URL",
          "value": "http://profile-service:8082/api/v1"
        },
        {
          "name": "MATCH_SERVICE_URL",
          "value": "http://match-service:8083/api/v1"
        },
        {
          "name": "NOTIFICATION_SERVICE_URL",
          "value": "http://notification-service:8084/api/v1"
        }
      ]
    },
    {
      "name": "auth-service",
      "image": "<your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-auth-service:latest",
      "essential": true,
      "memory": 128,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://<your-rds-endpoint>:5432/roomie_auth"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "value": "postgres"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "value": "<your-db-password>"
        },
        {
          "name": "JWT_SECRET",
          "value": "<your-jwt-secret>"
        }
      ]
    },
    {
      "name": "profile-service",
      "image": "<your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-profile-service:latest",
      "essential": true,
      "memory": 128,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://<your-rds-endpoint>:5432/roomie_profile"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "value": "postgres"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "value": "<your-db-password>"
        },
        {
          "name": "JWT_SECRET",
          "value": "<your-jwt-secret>"
        }
      ]
    },
    {
      "name": "match-service",
      "image": "<your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-match-service:latest",
      "essential": true,
      "memory": 128,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://<your-rds-endpoint>:5432/roomie_match"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "value": "postgres"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "value": "<your-db-password>"
        },
        {
          "name": "JWT_SECRET",
          "value": "<your-jwt-secret>"
        }
      ]
    },
    {
      "name": "notification-service",
      "image": "<your-account-id>.dkr.ecr.<your-region>.amazonaws.com/roomiematcher-notification-service:latest",
      "essential": true,
      "memory": 128,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://<your-rds-endpoint>:5432/roomie_notification"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "value": "postgres"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "value": "<your-db-password>"
        },
        {
          "name": "JWT_SECRET",
          "value": "<your-jwt-secret>"
        },
        {
          "name": "AWS_SES_ACCESS_KEY",
          "value": "<your-ses-access-key>"
        },
        {
          "name": "AWS_SES_SECRET_KEY",
          "value": "<your-ses-secret-key>"
        },
        {
          "name": "AWS_SES_REGION",
          "value": "<your-region>"
        },
        {
          "name": "AWS_SES_FROM_EMAIL",
          "value": "<your-verified-email>"
        }
      ]
    }
  ]
}
```

### 2.5 Create Deployment Package

```bash
# Create a directory for the deployment package
mkdir -p deploy_package

# Copy .ebextensions directory if it exists
cp -r .ebextensions deploy_package/ || echo "No .ebextensions directory found"

# Copy Dockerrun.aws.json
cp Dockerrun.aws.json deploy_package/

# Create ZIP archive
cd deploy_package
zip -r ../deploy.zip .
cd ..
```

## Step 3: Deploy to Elastic Beanstalk

### 3.1 Create Elastic Beanstalk Application

1. Navigate to Elastic Beanstalk in the AWS Console
2. Click "Create application"
3. Enter application name: `roomiematcher`
4. Click "Create"

### 3.2 Create Environment

1. Click "Create environment"
2. Select "Web server environment"
3. Configure the following:
   - Environment name: `roomiematcher-prod`
   - Domain: (leave as default or customize)
   - Platform: Docker
   - Application code: Upload your code
   - Upload your `deploy.zip` file
4. Click "Configure more options"
5. Under "Software" click "Edit" and add the following environment variables:
   - `JWT_SECRET`: (your JWT secret)
   - `SPRING_PROFILES_ACTIVE`: `prod`
6. Under "Capacity" click "Edit" and select:
   - Environment type: Single instance
   - Instance type: t2.micro (Free tier eligible)
7. Under "Load balancer" click "Edit" and select:
   - Load balancer type: None (Single instance environments don't use a load balancer)
8. Click "Create environment"

### 3.3 Monitor Deployment

1. Wait for the environment to be created (this may take 5-10 minutes)
2. Monitor the events log for any errors
3. Once the environment is "Green/Healthy", click on the URL to access your application

## Step 4: Post-Deployment Tasks

### 4.1 Configure Security

1. Navigate to EC2 → Security Groups
2. Find the security group for your Elastic Beanstalk environment
3. Update inbound rules to restrict access as needed

### 4.2 Set Up Monitoring

1. Navigate to CloudWatch
2. Create alarms for:
   - CPU utilization
   - Memory usage
   - Request count
   - Error rates

### 4.3 Set Up Auto Scaling

1. Navigate to Elastic Beanstalk → Your environment → Configuration
2. Under "Capacity" click "Edit"
3. Configure scaling triggers based on CPU utilization or network traffic

## Troubleshooting

### Common Issues

1. **Connection Timeout to RDS**
   - Check security groups to ensure Elastic Beanstalk can access RDS
   - Verify RDS endpoint is correct in Dockerrun.aws.json

2. **Docker Container Failures**
   - Check Elastic Beanstalk logs for container startup issues
   - Verify ECR repository URLs are correct

3. **Application Errors**
   - Check application logs in CloudWatch
   - SSH into the EC2 instance for direct troubleshooting

### Viewing Logs

1. Navigate to Elastic Beanstalk → Your environment → Logs
2. Request logs or view existing logs
3. For more detailed logs, use CloudWatch Logs

## Updating Your Application

To update your application:

1. Make code changes
2. Build and push new Docker images to ECR
3. Create a new deployment package
4. Upload the new package to Elastic Beanstalk:
   - Navigate to your environment
   - Click "Upload and deploy"
   - Select your new ZIP file
   - Enter a version label
   - Click "Deploy"

## Cleanup

When you no longer need the application:

1. Navigate to Elastic Beanstalk
2. Terminate your environment
3. Delete the application
4. Delete ECR repositories
5. Delete the RDS instance
6. Delete any associated IAM roles or users 
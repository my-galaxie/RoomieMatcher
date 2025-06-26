# AWS Free Tier Optimizations for RoomieMatcher

This document outlines all the optimizations and configurations made to ensure RoomieMatcher can be deployed within the AWS Free Tier limits.

## 1. Elastic Beanstalk Configuration

### 1.1 Single Instance Environment
- Configured to use a single instance environment instead of a load-balanced environment
- Removed load balancer configuration to stay within free tier limits
- Updated `env-config-minimal.json` to use:
  ```json
  {
    "Namespace": "aws:elasticbeanstalk:environment",
    "OptionName": "EnvironmentType",
    "Value": "SingleInstance"
  },
  {
    "Namespace": "aws:elasticbeanstalk:environment",
    "OptionName": "LoadBalancerType",
    "Value": "none"
  }
  ```

### 1.2 Instance Type
- Using t2.micro instance type (eligible for free tier)
- Configured in `env-config-minimal.json`:
  ```json
  {
    "Namespace": "aws:autoscaling:launchconfiguration",
    "OptionName": "InstanceType",
    "Value": "t2.micro"
  }
  ```

### 1.3 Spot Instances
- Enabled spot instances to reduce costs further:
  ```json
  {
    "Namespace": "aws:ec2:instances",
    "OptionName": "EnableSpot",
    "Value": "true"
  },
  {
    "Namespace": "aws:ec2:instances",
    "OptionName": "SpotMaxPrice",
    "Value": "0.0035"
  }
  ```

### 1.4 Basic Health Reporting
- Using basic health reporting instead of enhanced to avoid CloudWatch costs:
  ```json
  {
    "Namespace": "aws:elasticbeanstalk:healthreporting:system",
    "OptionName": "SystemType",
    "Value": "basic"
  }
  ```

### 1.5 Deployment Policy
- Using "AllAtOnce" deployment policy for single instance environment:
  ```json
  {
    "Namespace": "aws:elasticbeanstalk:command",
    "OptionName": "DeploymentPolicy",
    "Value": "AllAtOnce"
  }
  ```

## 2. Database Configuration

### 2.1 RDS Instance
- Using db.t3.micro instance type (eligible for free tier)
- Disabled Multi-AZ deployment to stay within free tier
- Disabled storage encryption to stay within free tier
- Limited storage to 20GB to stay within free tier

### 2.2 Connection Pooling
- Optimized connection pooling in application.yml files:
  ```yaml
  spring:
    datasource:
      hikari:
        maximum-pool-size: 5
        connection-timeout: 30000
  ```

## 3. Docker Optimizations

### 3.1 Container Memory Limits
- Limited container memory in Dockerrun.aws.json:
  ```json
  {
    "memory": 128
  }
  ```

### 3.2 JVM Memory Settings
- Optimized JVM memory settings in Dockerfiles:
  ```
  ENV JAVA_OPTS="-Xms96m -Xmx128m -XX:+UseG1GC -XX:MaxGCPauseMillis=100"
  ```

## 4. CI/CD Configuration

### 4.1 GitHub Actions
- Updated GitHub Actions workflow to use SingleInstance environment type
- Configured to use minimal environment settings

### 4.2 Manual Deployment
- Updated manual deployment guide to specify single instance environment
- Removed load balancer configuration from manual deployment steps

## 5. AWS SES Configuration

### 5.1 SES in Sandbox Mode
- Configured notification service to work with SES in sandbox mode
- Added verification handling for recipient emails
- Improved error handling for unverified emails

## 6. Monitoring and Cost Control

### 6.1 Minimal Logging
- Reduced logging levels in production to minimize CloudWatch costs
- Configured log retention to minimize storage costs

### 6.2 AWS Budget Alerts
- Added instructions for setting up AWS Budget alerts to monitor costs

## Deployment Instructions

To deploy RoomieMatcher within AWS Free Tier limits:

1. **GitHub CI/CD**: Follow the instructions in `GITHUB_DEPLOYMENT_README.md`, which will automatically use the free tier optimized configuration.

2. **Manual Deployment**: Follow the step-by-step guide in `MANUAL_BEANSTALK_DEPLOYMENT.md`, which has been updated for free tier deployment.

## Cost Monitoring

After deployment, monitor your AWS costs regularly:

1. Set up AWS Budget alerts to notify you if costs approach free tier limits
2. Use the `monitor-free-tier.sh` script to check current usage
3. Consider running the application only when needed during development

## Important Notes

- AWS Free Tier limits change over time. Always check the latest AWS Free Tier documentation.
- The first 12 months of a new AWS account offer the most generous free tier benefits.
- Some services may incur minimal charges even with these optimizations. 
#!/bin/bash

# RoomieMatcher Deployment Configuration Example
# Copy this file to config.sh and update with your specific values

# AWS Configuration
export AWS_REGION="us-east-1"
export AWS_ACCOUNT_ID="123456789012"  # Your AWS account ID

# Environment Configuration
export ENVIRONMENT_NAME="dev"  # dev, staging, or prod

# GitHub Configuration (for CI/CD pipeline)
export GITHUB_OWNER="your-github-username"
export GITHUB_REPO="roomiematcher"
export GITHUB_BRANCH="main"
export GITHUB_TOKEN="your-github-token"  # OAuth token with repo access

# Email Configuration
export AWS_SES_REGION="us-east-1"  # Region where SES is configured
export AWS_SES_FROM_EMAIL="no-reply@roomiematcher.com"  # Verified SES email

# Database Configuration
# Note: These values should match what you've set in AWS Secrets Manager
export DB_MASTER_USERNAME="postgres"
export DB_MASTER_PASSWORD="your-secure-password"

# JWT Configuration
# Note: This value should match what you've set in AWS Secrets Manager
export JWT_SECRET="your-jwt-secret-key"

# AWS SES Configuration
# Note: These values should match what you've set in AWS Secrets Manager
export AWS_SES_ACCESS_KEY="your-ses-access-key"
export AWS_SES_SECRET_KEY="your-ses-secret-key"

# Usage:
# 1. Copy this file: cp config-example.sh config-dev.sh
# 2. Edit the values: vim config-dev.sh
# 3. Source the file: source config-dev.sh
# 4. Run the deployment script: ./deploy.sh 
#!/bin/bash

# RoomieMatcher AWS Deployment Configuration Example
# Copy this file and update with your specific values

# Environment (dev, staging, prod)
export ENVIRONMENT="dev"

# AWS Region
export REGION="us-east-1"

# GitHub Repository Information
export GITHUB_OWNER="your-github-username"
export GITHUB_REPO="roomiematcher"
export GITHUB_BRANCH="main"
export GITHUB_TOKEN="your-github-oauth-token"

# S3 Bucket for Artifacts
export ARTIFACT_BUCKET="roomie-matcher-artifacts"

# JWT Secret for Token Signing
export JWT_SECRET="your-jwt-secret-key"

# Database Credentials
export DB_USERNAME="postgres"
export DB_PASSWORD="your-database-password"

# AWS SES Credentials
export SES_ACCESS_KEY="your-ses-access-key"
export SES_SECRET_KEY="your-ses-secret-key"
export SES_FROM_EMAIL="no-reply@yourdomain.com"

# Usage:
# 1. Copy this file: cp config-example.sh config-dev.sh
# 2. Edit the values: vim config-dev.sh
# 3. Source the file: source config-dev.sh
# 4. Run the deployment script: ./deploy.sh 
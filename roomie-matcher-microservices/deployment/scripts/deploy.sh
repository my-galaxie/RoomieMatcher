#!/bin/bash

# RoomieMatcher AWS Deployment Script
# This script deploys the RoomieMatcher microservices infrastructure to AWS

set -e

# Default values
ENVIRONMENT="dev"
REGION="us-east-1"
GITHUB_OWNER=""
GITHUB_REPO="roomiematcher"
GITHUB_BRANCH="main"
GITHUB_TOKEN=""
ARTIFACT_BUCKET="roomie-matcher-artifacts"
JWT_SECRET=""
DB_USERNAME="postgres"
DB_PASSWORD=""
SES_ACCESS_KEY=""
SES_SECRET_KEY=""
SES_FROM_EMAIL="no-reply@roomiematcher.com"

# Display help
function display_help() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  -e, --environment     Environment (dev, staging, prod) [default: dev]"
    echo "  -r, --region          AWS Region [default: us-east-1]"
    echo "  -o, --github-owner    GitHub repository owner [required]"
    echo "  -g, --github-repo     GitHub repository name [default: roomiematcher]"
    echo "  -b, --github-branch   GitHub repository branch [default: main]"
    echo "  -t, --github-token    GitHub OAuth token [required]"
    echo "  -a, --artifact-bucket S3 bucket for artifacts [default: roomie-matcher-artifacts]"
    echo "  -j, --jwt-secret      JWT secret for token signing [required]"
    echo "  -u, --db-username     Database username [default: postgres]"
    echo "  -p, --db-password     Database password [required]"
    echo "  -k, --ses-access-key  AWS SES Access Key [required]"
    echo "  -s, --ses-secret-key  AWS SES Secret Key [required]"
    echo "  -f, --ses-from-email  AWS SES From Email [default: no-reply@roomiematcher.com]"
    echo "  -h, --help            Display this help message"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -e|--environment)
            ENVIRONMENT="$2"
            shift
            shift
            ;;
        -r|--region)
            REGION="$2"
            shift
            shift
            ;;
        -o|--github-owner)
            GITHUB_OWNER="$2"
            shift
            shift
            ;;
        -g|--github-repo)
            GITHUB_REPO="$2"
            shift
            shift
            ;;
        -b|--github-branch)
            GITHUB_BRANCH="$2"
            shift
            shift
            ;;
        -t|--github-token)
            GITHUB_TOKEN="$2"
            shift
            shift
            ;;
        -a|--artifact-bucket)
            ARTIFACT_BUCKET="$2"
            shift
            shift
            ;;
        -j|--jwt-secret)
            JWT_SECRET="$2"
            shift
            shift
            ;;
        -u|--db-username)
            DB_USERNAME="$2"
            shift
            shift
            ;;
        -p|--db-password)
            DB_PASSWORD="$2"
            shift
            shift
            ;;
        -k|--ses-access-key)
            SES_ACCESS_KEY="$2"
            shift
            shift
            ;;
        -s|--ses-secret-key)
            SES_SECRET_KEY="$2"
            shift
            shift
            ;;
        -f|--ses-from-email)
            SES_FROM_EMAIL="$2"
            shift
            shift
            ;;
        -h|--help)
            display_help
            ;;
        *)
            echo "Unknown option: $1"
            display_help
            ;;
    esac
done

# Validate required parameters
if [[ -z "$GITHUB_OWNER" || -z "$GITHUB_TOKEN" || -z "$JWT_SECRET" || -z "$DB_PASSWORD" || -z "$SES_ACCESS_KEY" || -z "$SES_SECRET_KEY" ]]; then
    echo "Error: Missing required parameters"
    display_help
fi

# Set AWS region
echo "Setting AWS region to $REGION"
aws configure set region $REGION

# Create AWS Secrets Manager secrets
echo "Creating AWS Secrets Manager secrets..."

# JWT Secret
echo "Creating JWT secret..."
aws secretsmanager create-secret --name "${ENVIRONMENT}-roomie-matcher-jwt" \
    --description "JWT secret for RoomieMatcher ${ENVIRONMENT}" \
    --secret-string "{\"secret\":\"${JWT_SECRET}\"}" \
    --region $REGION || \
aws secretsmanager update-secret --secret-id "${ENVIRONMENT}-roomie-matcher-jwt" \
    --secret-string "{\"secret\":\"${JWT_SECRET}\"}" \
    --region $REGION

# Database Credentials
echo "Creating database credentials secret..."
aws secretsmanager create-secret --name "${ENVIRONMENT}-roomie-matcher-db" \
    --description "Database credentials for RoomieMatcher ${ENVIRONMENT}" \
    --secret-string "{\"username\":\"${DB_USERNAME}\",\"password\":\"${DB_PASSWORD}\"}" \
    --region $REGION || \
aws secretsmanager update-secret --secret-id "${ENVIRONMENT}-roomie-matcher-db" \
    --secret-string "{\"username\":\"${DB_USERNAME}\",\"password\":\"${DB_PASSWORD}\"}" \
    --region $REGION

# AWS SES Credentials
echo "Creating AWS SES credentials secret..."
aws secretsmanager create-secret --name "${ENVIRONMENT}-roomie-matcher-ses" \
    --description "AWS SES credentials for RoomieMatcher ${ENVIRONMENT}" \
    --secret-string "{\"accessKey\":\"${SES_ACCESS_KEY}\",\"secretKey\":\"${SES_SECRET_KEY}\"}" \
    --region $REGION || \
aws secretsmanager update-secret --secret-id "${ENVIRONMENT}-roomie-matcher-ses" \
    --secret-string "{\"accessKey\":\"${SES_ACCESS_KEY}\",\"secretKey\":\"${SES_SECRET_KEY}\"}" \
    --region $REGION

# Deploy CI/CD Pipeline
echo "Deploying CI/CD pipeline..."
aws cloudformation deploy \
    --template-file ../cloudformation/pipeline/cicd-pipeline.yaml \
    --stack-name "${ENVIRONMENT}-roomie-matcher-pipeline" \
    --parameter-overrides \
        EnvironmentName=$ENVIRONMENT \
        GitHubOwner=$GITHUB_OWNER \
        GitHubRepo=$GITHUB_REPO \
        GitHubBranch=$GITHUB_BRANCH \
        GitHubToken=$GITHUB_TOKEN \
        ArtifactBucketName=$ARTIFACT_BUCKET \
    --capabilities CAPABILITY_NAMED_IAM \
    --region $REGION

echo "Deployment initiated successfully!"
echo "The CI/CD pipeline will now deploy the following stacks:"
echo "  - ${ENVIRONMENT}-roomie-matcher-vpc"
echo "  - ${ENVIRONMENT}-roomie-matcher-rds"
echo "  - ${ENVIRONMENT}-roomie-matcher-ecs-cluster"
echo "  - ${ENVIRONMENT}-roomie-matcher-service-discovery"
echo "  - ${ENVIRONMENT}-roomie-matcher-task-definitions"
echo "  - ${ENVIRONMENT}-roomie-matcher-load-balancer"
echo ""
echo "You can monitor the deployment progress in the AWS CodePipeline console:"
echo "https://console.aws.amazon.com/codepipeline/home?region=${REGION}#/view/${ENVIRONMENT}-roomie-matcher-pipeline" 
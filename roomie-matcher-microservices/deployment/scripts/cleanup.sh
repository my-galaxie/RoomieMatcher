#!/bin/bash

# RoomieMatcher AWS Cleanup Script
# This script deletes all AWS resources created by the deployment

set -e

# Default values
ENVIRONMENT="dev"
REGION="us-east-1"

# Display help
function display_help() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  -e, --environment     Environment (dev, staging, prod) [default: dev]"
    echo "  -r, --region          AWS Region [default: us-east-1]"
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
        -h|--help)
            display_help
            ;;
        *)
            echo "Unknown option: $1"
            display_help
            ;;
    esac
done

# Set AWS region
echo "Setting AWS region to $REGION"
aws configure set region $REGION

# Confirm deletion
echo "WARNING: This will delete all AWS resources created for the RoomieMatcher ${ENVIRONMENT} environment in ${REGION}."
echo "This action cannot be undone and may result in data loss."
read -p "Are you sure you want to proceed? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cleanup aborted."
    exit 1
fi

# Delete CloudFormation stacks in reverse order
echo "Deleting CloudFormation stacks..."

echo "Deleting load balancer stack..."
aws cloudformation delete-stack --stack-name "${ENVIRONMENT}-roomie-matcher-load-balancer" --region $REGION
echo "Waiting for load balancer stack deletion to complete..."
aws cloudformation wait stack-delete-complete --stack-name "${ENVIRONMENT}-roomie-matcher-load-balancer" --region $REGION

echo "Deleting task definitions stack..."
aws cloudformation delete-stack --stack-name "${ENVIRONMENT}-roomie-matcher-task-definitions" --region $REGION
echo "Waiting for task definitions stack deletion to complete..."
aws cloudformation wait stack-delete-complete --stack-name "${ENVIRONMENT}-roomie-matcher-task-definitions" --region $REGION

echo "Deleting service discovery stack..."
aws cloudformation delete-stack --stack-name "${ENVIRONMENT}-roomie-matcher-service-discovery" --region $REGION
echo "Waiting for service discovery stack deletion to complete..."
aws cloudformation wait stack-delete-complete --stack-name "${ENVIRONMENT}-roomie-matcher-service-discovery" --region $REGION

echo "Deleting ECS cluster stack..."
aws cloudformation delete-stack --stack-name "${ENVIRONMENT}-roomie-matcher-ecs-cluster" --region $REGION
echo "Waiting for ECS cluster stack deletion to complete..."
aws cloudformation wait stack-delete-complete --stack-name "${ENVIRONMENT}-roomie-matcher-ecs-cluster" --region $REGION

echo "Deleting RDS stack..."
aws cloudformation delete-stack --stack-name "${ENVIRONMENT}-roomie-matcher-rds" --region $REGION
echo "Waiting for RDS stack deletion to complete..."
aws cloudformation wait stack-delete-complete --stack-name "${ENVIRONMENT}-roomie-matcher-rds" --region $REGION

echo "Deleting VPC stack..."
aws cloudformation delete-stack --stack-name "${ENVIRONMENT}-roomie-matcher-vpc" --region $REGION
echo "Waiting for VPC stack deletion to complete..."
aws cloudformation wait stack-delete-complete --stack-name "${ENVIRONMENT}-roomie-matcher-vpc" --region $REGION

echo "Deleting pipeline stack..."
aws cloudformation delete-stack --stack-name "${ENVIRONMENT}-roomie-matcher-pipeline" --region $REGION
echo "Waiting for pipeline stack deletion to complete..."
aws cloudformation wait stack-delete-complete --stack-name "${ENVIRONMENT}-roomie-matcher-pipeline" --region $REGION

# Delete AWS Secrets Manager secrets
echo "Deleting AWS Secrets Manager secrets..."

echo "Deleting JWT secret..."
aws secretsmanager delete-secret --secret-id "${ENVIRONMENT}-roomie-matcher-jwt" --force-delete-without-recovery --region $REGION || true

echo "Deleting database credentials secret..."
aws secretsmanager delete-secret --secret-id "${ENVIRONMENT}-roomie-matcher-db" --force-delete-without-recovery --region $REGION || true

echo "Deleting AWS SES credentials secret..."
aws secretsmanager delete-secret --secret-id "${ENVIRONMENT}-roomie-matcher-ses" --force-delete-without-recovery --region $REGION || true

# Delete ECR repositories
echo "Deleting ECR repositories..."

echo "Deleting auth service repository..."
aws ecr delete-repository --repository-name "${ENVIRONMENT}-auth-service" --force --region $REGION || true

echo "Deleting profile service repository..."
aws ecr delete-repository --repository-name "${ENVIRONMENT}-profile-service" --force --region $REGION || true

echo "Deleting match service repository..."
aws ecr delete-repository --repository-name "${ENVIRONMENT}-match-service" --force --region $REGION || true

echo "Deleting notification service repository..."
aws ecr delete-repository --repository-name "${ENVIRONMENT}-notification-service" --force --region $REGION || true

echo "Deleting API gateway repository..."
aws ecr delete-repository --repository-name "${ENVIRONMENT}-api-gateway" --force --region $REGION || true

# Delete S3 bucket
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
BUCKET_NAME="roomie-matcher-artifacts-${ACCOUNT_ID}-${ENVIRONMENT}"

echo "Emptying and deleting S3 bucket ${BUCKET_NAME}..."
aws s3 rm s3://${BUCKET_NAME} --recursive --region $REGION || true
aws s3 rb s3://${BUCKET_NAME} --force --region $REGION || true

echo "Cleanup completed successfully!" 
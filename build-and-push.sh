#!/bin/bash

# Script to build and push Docker images for all services
# This script should be run from the root directory of the project

# Set variables
ECR_REGISTRY=${1:-"your-aws-account-id.dkr.ecr.your-region.amazonaws.com"}
IMAGE_TAG=${2:-"latest"}

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install Docker and try again."
    exit 1
fi

# Build and push API Gateway Service
echo "Building API Gateway Service..."
docker build -t $ECR_REGISTRY/roomiematcher-api-gateway:$IMAGE_TAG -t $ECR_REGISTRY/roomiematcher-api-gateway:latest -f api-gateway-service/Dockerfile .
docker push $ECR_REGISTRY/roomiematcher-api-gateway:$IMAGE_TAG
docker push $ECR_REGISTRY/roomiematcher-api-gateway:latest

# Build and push Auth Service
echo "Building Auth Service..."
docker build -t $ECR_REGISTRY/roomiematcher-auth-service:$IMAGE_TAG -t $ECR_REGISTRY/roomiematcher-auth-service:latest -f auth-service/Dockerfile .
docker push $ECR_REGISTRY/roomiematcher-auth-service:$IMAGE_TAG
docker push $ECR_REGISTRY/roomiematcher-auth-service:latest

# Build and push Profile Service
echo "Building Profile Service..."
docker build -t $ECR_REGISTRY/roomiematcher-profile-service:$IMAGE_TAG -t $ECR_REGISTRY/roomiematcher-profile-service:latest -f profile-service/Dockerfile .
docker push $ECR_REGISTRY/roomiematcher-profile-service:$IMAGE_TAG
docker push $ECR_REGISTRY/roomiematcher-profile-service:latest

# Build and push Match Service
echo "Building Match Service..."
docker build -t $ECR_REGISTRY/roomiematcher-match-service:$IMAGE_TAG -t $ECR_REGISTRY/roomiematcher-match-service:latest -f match-service/Dockerfile .
docker push $ECR_REGISTRY/roomiematcher-match-service:$IMAGE_TAG
docker push $ECR_REGISTRY/roomiematcher-match-service:latest

# Build and push Notification Service
echo "Building Notification Service..."
docker build -t $ECR_REGISTRY/roomiematcher-notification-service:$IMAGE_TAG -t $ECR_REGISTRY/roomiematcher-notification-service:latest -f notification-service/Dockerfile .
docker push $ECR_REGISTRY/roomiematcher-notification-service:$IMAGE_TAG
docker push $ECR_REGISTRY/roomiematcher-notification-service:latest

echo "All Docker images have been built and pushed successfully!" 
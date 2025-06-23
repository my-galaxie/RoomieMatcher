#!/bin/bash

# RoomieMatcher Deployment Script
# This script deploys the RoomieMatcher microservices to AWS using CloudFormation

set -e

# Default values
CONFIG_FILE="config.sh"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CFN_DIR="${SCRIPT_DIR}/../cloudformation"

# Display help
function show_help {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  -h, --help                 Show this help message"
    echo "  -c, --config CONFIG_FILE   Specify config file (default: config.sh)"
    echo "  -s, --skip-network         Skip network infrastructure deployment"
    echo "  -d, --skip-database        Skip database deployment"
    echo "  -e, --skip-cluster         Skip ECS cluster deployment"
    echo "  -t, --skip-tasks           Skip task definitions deployment"
    echo "  -l, --skip-loadbalancer    Skip load balancer deployment"
    echo "  -p, --skip-pipeline        Skip CI/CD pipeline deployment"
    exit 0
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -h|--help)
            show_help
            ;;
        -c|--config)
            CONFIG_FILE="$2"
            shift
            shift
            ;;
        -s|--skip-network)
            SKIP_NETWORK=true
            shift
            ;;
        -d|--skip-database)
            SKIP_DATABASE=true
            shift
            ;;
        -e|--skip-cluster)
            SKIP_CLUSTER=true
            shift
            ;;
        -t|--skip-tasks)
            SKIP_TASKS=true
            shift
            ;;
        -l|--skip-loadbalancer)
            SKIP_LOADBALANCER=true
            shift
            ;;
        -p|--skip-pipeline)
            SKIP_PIPELINE=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            ;;
    esac
done

# Load configuration
CONFIG_PATH="${SCRIPT_DIR}/${CONFIG_FILE}"
if [[ ! -f "${CONFIG_PATH}" ]]; then
    echo "Config file not found: ${CONFIG_PATH}"
    echo "Please create a config file based on config-example.sh"
    exit 1
fi

source "${CONFIG_PATH}"

# Validate required environment variables
required_vars=(
    "AWS_REGION"
    "ENVIRONMENT_NAME"
)

for var in "${required_vars[@]}"; do
    if [[ -z "${!var}" ]]; then
        echo "Error: ${var} is not set in ${CONFIG_PATH}"
        exit 1
    fi
done

echo "=== RoomieMatcher Deployment ==="
echo "Environment: ${ENVIRONMENT_NAME}"
echo "AWS Region: ${AWS_REGION}"
echo "================================"

# Function to wait for stack creation/update to complete
function wait_for_stack {
    local stack_name=$1
    echo "Waiting for stack ${stack_name} to complete..."
    
    aws cloudformation wait stack-create-complete --stack-name "${stack_name}" --region "${AWS_REGION}" 2>/dev/null || \
    aws cloudformation wait stack-update-complete --stack-name "${stack_name}" --region "${AWS_REGION}"
    
    if [[ $? -eq 0 ]]; then
        echo "Stack ${stack_name} completed successfully"
    else
        echo "Stack ${stack_name} failed to deploy"
        exit 1
    fi
}

# Deploy VPC and network infrastructure
if [[ "${SKIP_NETWORK}" != "true" ]]; then
    echo "Deploying network infrastructure..."
    
    aws cloudformation deploy \
        --template-file "${CFN_DIR}/network/vpc.yaml" \
        --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-vpc" \
        --parameter-overrides \
            EnvironmentName="${ENVIRONMENT_NAME}" \
        --capabilities CAPABILITY_IAM \
        --region "${AWS_REGION}"
    
    wait_for_stack "${ENVIRONMENT_NAME}-roomie-matcher-vpc"
fi

# Deploy RDS database
if [[ "${SKIP_DATABASE}" != "true" ]]; then
    echo "Deploying database infrastructure..."
    
    aws cloudformation deploy \
        --template-file "${CFN_DIR}/database/rds.yaml" \
        --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-rds" \
        --parameter-overrides \
            EnvironmentName="${ENVIRONMENT_NAME}" \
            DBMasterUsername="{{resolve:secretsmanager:${ENVIRONMENT_NAME}-roomie-matcher-db:SecretString:username}}" \
            DBMasterPassword="{{resolve:secretsmanager:${ENVIRONMENT_NAME}-roomie-matcher-db:SecretString:password}}" \
        --capabilities CAPABILITY_IAM \
        --region "${AWS_REGION}"
    
    wait_for_stack "${ENVIRONMENT_NAME}-roomie-matcher-rds"
fi

# Deploy ECS Cluster
if [[ "${SKIP_CLUSTER}" != "true" ]]; then
    echo "Deploying ECS cluster..."
    
    aws cloudformation deploy \
        --template-file "${CFN_DIR}/services/ecs-cluster.yaml" \
        --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-ecs-cluster" \
        --parameter-overrides \
            EnvironmentName="${ENVIRONMENT_NAME}" \
        --capabilities CAPABILITY_IAM \
        --region "${AWS_REGION}"
    
    wait_for_stack "${ENVIRONMENT_NAME}-roomie-matcher-ecs-cluster"
fi

# Deploy service discovery and task definitions
if [[ "${SKIP_TASKS}" != "true" ]]; then
    echo "Deploying service discovery..."
    
    aws cloudformation deploy \
        --template-file "${CFN_DIR}/services/ecs-services.yaml" \
        --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-service-discovery" \
        --parameter-overrides \
            EnvironmentName="${ENVIRONMENT_NAME}" \
        --capabilities CAPABILITY_IAM \
        --region "${AWS_REGION}"
    
    wait_for_stack "${ENVIRONMENT_NAME}-roomie-matcher-service-discovery"
    
    echo "Deploying task definitions..."
    
    # Get ECR repository URIs
    AUTH_SERVICE_IMAGE="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ENVIRONMENT_NAME}-auth-service:latest"
    PROFILE_SERVICE_IMAGE="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ENVIRONMENT_NAME}-profile-service:latest"
    MATCH_SERVICE_IMAGE="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ENVIRONMENT_NAME}-match-service:latest"
    NOTIFICATION_SERVICE_IMAGE="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ENVIRONMENT_NAME}-notification-service:latest"
    API_GATEWAY_IMAGE="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ENVIRONMENT_NAME}-api-gateway:latest"
    
    aws cloudformation deploy \
        --template-file "${CFN_DIR}/services/ecs-task-definitions.yaml" \
        --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-task-definitions" \
        --parameter-overrides \
            EnvironmentName="${ENVIRONMENT_NAME}" \
            AuthServiceImage="${AUTH_SERVICE_IMAGE}" \
            ProfileServiceImage="${PROFILE_SERVICE_IMAGE}" \
            MatchServiceImage="${MATCH_SERVICE_IMAGE}" \
            NotificationServiceImage="${NOTIFICATION_SERVICE_IMAGE}" \
            ApiGatewayImage="${API_GATEWAY_IMAGE}" \
            JwtSecret="{{resolve:secretsmanager:${ENVIRONMENT_NAME}-roomie-matcher-jwt:SecretString:secret}}" \
            DBMasterUsername="{{resolve:secretsmanager:${ENVIRONMENT_NAME}-roomie-matcher-db:SecretString:username}}" \
            DBMasterPassword="{{resolve:secretsmanager:${ENVIRONMENT_NAME}-roomie-matcher-db:SecretString:password}}" \
            AwsSesAccessKey="{{resolve:secretsmanager:${ENVIRONMENT_NAME}-roomie-matcher-ses:SecretString:accessKey}}" \
            AwsSesSecretKey="{{resolve:secretsmanager:${ENVIRONMENT_NAME}-roomie-matcher-ses:SecretString:secretKey}}" \
            AwsSesRegion="${AWS_SES_REGION}" \
            AwsSesFromEmail="${AWS_SES_FROM_EMAIL}" \
        --capabilities CAPABILITY_IAM \
        --region "${AWS_REGION}"
    
    wait_for_stack "${ENVIRONMENT_NAME}-roomie-matcher-task-definitions"
fi

# Deploy load balancer
if [[ "${SKIP_LOADBALANCER}" != "true" ]]; then
    echo "Deploying load balancer..."
    
    aws cloudformation deploy \
        --template-file "${CFN_DIR}/services/load-balancer.yaml" \
        --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-load-balancer" \
        --parameter-overrides \
            EnvironmentName="${ENVIRONMENT_NAME}" \
        --capabilities CAPABILITY_IAM \
        --region "${AWS_REGION}"
    
    wait_for_stack "${ENVIRONMENT_NAME}-roomie-matcher-load-balancer"
fi

# Deploy CI/CD pipeline
if [[ "${SKIP_PIPELINE}" != "true" && -n "${GITHUB_OWNER}" && -n "${GITHUB_TOKEN}" ]]; then
    echo "Deploying CI/CD pipeline..."
    
    aws cloudformation deploy \
        --template-file "${CFN_DIR}/pipeline/cicd-pipeline.yaml" \
        --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-pipeline" \
        --parameter-overrides \
            EnvironmentName="${ENVIRONMENT_NAME}" \
            GitHubOwner="${GITHUB_OWNER}" \
            GitHubRepo="${GITHUB_REPO:-roomiematcher}" \
            GitHubBranch="${GITHUB_BRANCH:-main}" \
            GitHubToken="${GITHUB_TOKEN}" \
            ArtifactBucketName="roomie-matcher-artifacts" \
        --capabilities CAPABILITY_IAM \
        --region "${AWS_REGION}"
    
    wait_for_stack "${ENVIRONMENT_NAME}-roomie-matcher-pipeline"
fi

# Get the load balancer URL
echo "Deployment completed successfully!"
echo "Getting load balancer URL..."

LB_URL=$(aws cloudformation describe-stacks \
    --stack-name "${ENVIRONMENT_NAME}-roomie-matcher-load-balancer" \
    --query "Stacks[0].Outputs[?OutputKey=='LoadBalancerDNS'].OutputValue" \
    --output text \
    --region "${AWS_REGION}")

echo "Application is available at: http://${LB_URL}"
echo "API Gateway endpoint: http://${LB_URL}/api/v1" 
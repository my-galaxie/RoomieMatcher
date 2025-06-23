#!/bin/bash

# RoomieMatcher Cleanup Script
# This script removes all AWS resources created for the RoomieMatcher microservices

set -e

# Default values
CONFIG_FILE="config.sh"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Display help
function show_help {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  -h, --help                 Show this help message"
    echo "  -c, --config CONFIG_FILE   Specify config file (default: config.sh)"
    echo "  -f, --force                Skip confirmation prompt"
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
        -f|--force)
            FORCE=true
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
if [[ -z "${AWS_REGION}" || -z "${ENVIRONMENT_NAME}" ]]; then
    echo "Error: AWS_REGION and ENVIRONMENT_NAME must be set in ${CONFIG_PATH}"
    exit 1
fi

echo "=== RoomieMatcher Cleanup ==="
echo "Environment: ${ENVIRONMENT_NAME}"
echo "AWS Region: ${AWS_REGION}"
echo "=============================="

# Confirm deletion
if [[ "${FORCE}" != "true" ]]; then
    echo "WARNING: This will delete all RoomieMatcher resources in the ${ENVIRONMENT_NAME} environment."
    echo "         This action is IRREVERSIBLE and will result in DATA LOSS."
    echo ""
    read -p "Are you sure you want to continue? (y/N) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Cleanup aborted."
        exit 0
    fi
fi

# Function to delete a CloudFormation stack and wait for completion
function delete_stack {
    local stack_name=$1
    
    # Check if stack exists
    if aws cloudformation describe-stacks --stack-name "${stack_name}" --region "${AWS_REGION}" &>/dev/null; then
        echo "Deleting stack: ${stack_name}..."
        aws cloudformation delete-stack --stack-name "${stack_name}" --region "${AWS_REGION}"
        
        echo "Waiting for stack deletion to complete..."
        aws cloudformation wait stack-delete-complete --stack-name "${stack_name}" --region "${AWS_REGION}"
        
        echo "Stack ${stack_name} deleted successfully."
    else
        echo "Stack ${stack_name} does not exist, skipping."
    fi
}

# Delete stacks in reverse order
echo "Deleting CI/CD pipeline..."
delete_stack "${ENVIRONMENT_NAME}-roomie-matcher-pipeline"

echo "Deleting load balancer..."
delete_stack "${ENVIRONMENT_NAME}-roomie-matcher-load-balancer"

echo "Deleting task definitions..."
delete_stack "${ENVIRONMENT_NAME}-roomie-matcher-task-definitions"

echo "Deleting service discovery..."
delete_stack "${ENVIRONMENT_NAME}-roomie-matcher-service-discovery"

echo "Deleting ECS cluster..."
delete_stack "${ENVIRONMENT_NAME}-roomie-matcher-ecs-cluster"

echo "Deleting database..."
delete_stack "${ENVIRONMENT_NAME}-roomie-matcher-rds"

echo "Deleting network infrastructure..."
delete_stack "${ENVIRONMENT_NAME}-roomie-matcher-vpc"

# Delete ECR repositories
if [[ "${FORCE}" == "true" || "${SKIP_ECR}" != "true" ]]; then
    echo "Deleting ECR repositories..."
    
    repositories=(
        "${ENVIRONMENT_NAME}-auth-service"
        "${ENVIRONMENT_NAME}-profile-service"
        "${ENVIRONMENT_NAME}-match-service"
        "${ENVIRONMENT_NAME}-notification-service"
        "${ENVIRONMENT_NAME}-api-gateway"
    )
    
    for repo in "${repositories[@]}"; do
        if aws ecr describe-repositories --repository-names "${repo}" --region "${AWS_REGION}" &>/dev/null; then
            echo "Deleting repository: ${repo}..."
            aws ecr delete-repository --repository-name "${repo}" --force --region "${AWS_REGION}"
        else
            echo "Repository ${repo} does not exist, skipping."
        fi
    done
fi

# Delete secrets
if [[ "${FORCE}" == "true" || "${SKIP_SECRETS}" != "true" ]]; then
    echo "Deleting secrets..."
    
    secrets=(
        "${ENVIRONMENT_NAME}-roomie-matcher-jwt"
        "${ENVIRONMENT_NAME}-roomie-matcher-db"
        "${ENVIRONMENT_NAME}-roomie-matcher-ses"
    )
    
    for secret in "${secrets[@]}"; do
        if aws secretsmanager describe-secret --secret-id "${secret}" --region "${AWS_REGION}" &>/dev/null; then
            echo "Deleting secret: ${secret}..."
            aws secretsmanager delete-secret --secret-id "${secret}" --force-delete-without-recovery --region "${AWS_REGION}"
        else
            echo "Secret ${secret} does not exist, skipping."
        fi
    done
fi

echo "Cleanup completed successfully!" 
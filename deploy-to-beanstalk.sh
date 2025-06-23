#!/bin/bash

# RoomieMatcher AWS Elastic Beanstalk Deployment Script
# This script helps deploy the RoomieMatcher application to AWS Elastic Beanstalk

set -e

# Default values
ENVIRONMENT="prod"
REGION="us-east-1"
APPLICATION_NAME="roomiematcher"
VERSION_LABEL="v$(date +%Y%m%d%H%M%S)"
S3_BUCKET=""
STACK_NAME="64bit Amazon Linux 2 v3.5.3 running Docker"

# Display help
function display_help() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  -e, --environment     Environment name [default: prod]"
    echo "  -r, --region          AWS Region [default: us-east-1]"
    echo "  -a, --application     Application name [default: roomiematcher]"
    echo "  -b, --bucket          S3 bucket name for deployment artifacts"
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
        -a|--application)
            APPLICATION_NAME="$2"
            shift
            shift
            ;;
        -b|--bucket)
            S3_BUCKET="$2"
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

# Check if S3 bucket is provided
if [ -z "$S3_BUCKET" ]; then
    echo "Error: S3 bucket name is required. Use -b or --bucket option."
    display_help
fi

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if AWS credentials are configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo "Error: AWS credentials are not configured. Run 'aws configure' first."
    exit 1
fi

# Set AWS region
echo "Setting AWS region to $REGION"
aws configure set region $REGION

# Create S3 bucket if it doesn't exist
echo "Checking if S3 bucket exists..."
if ! aws s3api head-bucket --bucket "$S3_BUCKET" 2>/dev/null; then
    echo "Creating S3 bucket: $S3_BUCKET"
    aws s3 mb "s3://$S3_BUCKET" --region $REGION
fi

# Get AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "AWS Account ID: $AWS_ACCOUNT_ID"

# Update Dockerrun.aws.json with AWS account ID and region
echo "Updating Dockerrun.aws.json with AWS account ID and region..."
sed -i "s|\${AWS_ACCOUNT_ID}|$AWS_ACCOUNT_ID|g" Dockerrun.aws.json
sed -i "s|\${AWS_REGION}|$REGION|g" Dockerrun.aws.json

# Create deployment package
echo "Creating deployment package..."
mkdir -p deploy_package
cp -r .ebextensions deploy_package/
cp Dockerrun.aws.json deploy_package/
cd deploy_package && zip -r ../deploy.zip .
cd ..

# Upload deployment package to S3
echo "Uploading deployment package to S3..."
aws s3 cp deploy.zip "s3://$S3_BUCKET/$VERSION_LABEL.zip" --region $REGION

# Check if application exists
echo "Checking if Elastic Beanstalk application exists..."
if ! aws elasticbeanstalk describe-applications --application-names "$APPLICATION_NAME" --region $REGION &>/dev/null; then
    echo "Creating Elastic Beanstalk application: $APPLICATION_NAME"
    aws elasticbeanstalk create-application --application-name "$APPLICATION_NAME" --region $REGION
fi

# Create application version
echo "Creating application version: $VERSION_LABEL"
aws elasticbeanstalk create-application-version \
    --application-name "$APPLICATION_NAME" \
    --version-label "$VERSION_LABEL" \
    --source-bundle S3Bucket="$S3_BUCKET",S3Key="$VERSION_LABEL.zip" \
    --region $REGION

# Check if environment exists
ENV_NAME="${APPLICATION_NAME}-${ENVIRONMENT}"
echo "Checking if Elastic Beanstalk environment exists: $ENV_NAME"
if aws elasticbeanstalk describe-environments --environment-names "$ENV_NAME" --region $REGION | grep -q "\"EnvironmentName\": \"$ENV_NAME\""; then
    # Update existing environment
    echo "Updating existing environment: $ENV_NAME"
    aws elasticbeanstalk update-environment \
        --environment-name "$ENV_NAME" \
        --version-label "$VERSION_LABEL" \
        --region $REGION
else
    # Create new environment
    echo "Creating new environment: $ENV_NAME"
    aws elasticbeanstalk create-environment \
        --application-name "$APPLICATION_NAME" \
        --environment-name "$ENV_NAME" \
        --solution-stack-name "$STACK_NAME" \
        --version-label "$VERSION_LABEL" \
        --region $REGION
fi

echo "Deployment initiated. You can monitor the status in the AWS Elastic Beanstalk console."
echo "Environment URL will be available at: https://$ENV_NAME.elasticbeanstalk.com"

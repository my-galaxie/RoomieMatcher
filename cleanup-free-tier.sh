#!/bin/bash

# RoomieMatcher AWS Free Tier Cleanup Script
# This script helps remove all AWS resources created for the RoomieMatcher deployment

set -e

# Configuration
APP_NAME="roomiematcher"
ENV_NAME="free-tier"
AWS_REGION="ap-south-1"  # Updated to match the user's region
DB_STACK_NAME="${APP_NAME}-db"
SES_USER_NAME="${APP_NAME}-ses-user"

echo "==== AWS Free Tier Cleanup for RoomieMatcher ===="
echo "This script will remove all AWS resources created for RoomieMatcher."
echo "WARNING: This will delete your database and all application data!"
read -p "Are you sure you want to continue? (y/n): " CONFIRM

if [[ $CONFIRM != "y" && $CONFIRM != "Y" ]]; then
    echo "Cleanup cancelled."
    exit 0
fi

# Check for AWS CLI
if ! command -v aws &> /dev/null; then
    echo "AWS CLI not found. Please install it first."
    exit 1
fi

# Check AWS account and permissions
echo "Checking AWS account..."
aws sts get-caller-identity --region $AWS_REGION || { echo "Failed to authenticate with AWS. Please check your credentials."; exit 1; }

# 1. Delete Elastic Beanstalk environment
echo "Step 1: Deleting Elastic Beanstalk environment..."
aws elasticbeanstalk describe-environments --environment-names "${APP_NAME}-${ENV_NAME}" --region $AWS_REGION &>/dev/null && \
aws elasticbeanstalk terminate-environment --environment-name "${APP_NAME}-${ENV_NAME}" --region $AWS_REGION

echo "Waiting for environment to be terminated..."
while true; do
    STATUS=$(aws elasticbeanstalk describe-environments --environment-names "${APP_NAME}-${ENV_NAME}" --query "Environments[0].Status" --output text --region $AWS_REGION 2>/dev/null || echo "TERMINATED")
    if [ "$STATUS" == "TERMINATED" ] || [ "$STATUS" == "None" ]; then
        break
    fi
    echo "Environment status: $STATUS"
    sleep 30
done

# 2. Delete Elastic Beanstalk application
echo "Step 2: Deleting Elastic Beanstalk application..."
aws elasticbeanstalk delete-application --application-name $APP_NAME --terminate-env-by-force --region $AWS_REGION || true

# 3. Delete RDS instance through CloudFormation
echo "Step 3: Deleting RDS instance..."
aws cloudformation delete-stack --stack-name $DB_STACK_NAME --region $AWS_REGION || true

echo "Waiting for RDS instance to be deleted..."
aws cloudformation wait stack-delete-complete --stack-name $DB_STACK_NAME --region $AWS_REGION || true

# 4. Delete SES user and credentials
echo "Step 4: Deleting SES IAM user and credentials..."
# Get user ARN
USER_ARN=$(aws iam get-user --user-name $SES_USER_NAME --query "User.Arn" --output text --region $AWS_REGION 2>/dev/null || echo "")

if [ -n "$USER_ARN" ] && [ "$USER_ARN" != "None" ]; then
    # List and delete access keys
    ACCESS_KEYS=$(aws iam list-access-keys --user-name $SES_USER_NAME --query "AccessKeyMetadata[*].AccessKeyId" --output text --region $AWS_REGION)
    for KEY_ID in $ACCESS_KEYS; do
        echo "Deleting access key $KEY_ID..."
        aws iam delete-access-key --user-name $SES_USER_NAME --access-key-id $KEY_ID --region $AWS_REGION
    done

    # Detach policies
    ATTACHED_POLICIES=$(aws iam list-attached-user-policies --user-name $SES_USER_NAME --query "AttachedPolicies[*].PolicyArn" --output text --region $AWS_REGION)
    for POLICY_ARN in $ATTACHED_POLICIES; do
        echo "Detaching policy $POLICY_ARN..."
        aws iam detach-user-policy --user-name $SES_USER_NAME --policy-arn $POLICY_ARN --region $AWS_REGION
    done

    # Delete user
    echo "Deleting IAM user $SES_USER_NAME..."
    aws iam delete-user --user-name $SES_USER_NAME --region $AWS_REGION
fi

# 5. Delete S3 buckets
echo "Step 5: Deleting S3 buckets..."
# Find and delete Elastic Beanstalk related buckets
EB_BUCKETS=$(aws s3api list-buckets --query "Buckets[?contains(Name, '${APP_NAME}') || contains(Name, 'elasticbeanstalk')].Name" --output text --region $AWS_REGION)
for BUCKET in $EB_BUCKETS; do
    echo "Emptying and deleting bucket $BUCKET..."
    aws s3 rm s3://$BUCKET --recursive --region $AWS_REGION || true
    aws s3api delete-bucket --bucket $BUCKET --region $AWS_REGION || true
done

echo "==== Cleanup Complete ===="
echo "Most resources have been deleted. Some resources like security groups may need manual cleanup."
echo "Check your AWS console to ensure all resources are properly removed." 
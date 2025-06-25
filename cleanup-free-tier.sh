#!/bin/bash

# RoomieMatcher AWS Free Tier Cleanup Script
# This script helps remove all AWS resources created for the RoomieMatcher deployment

set -e

# Configuration - update these values if you changed them in your deployment
APP_NAME="roomiematcher"
ENV_NAME="free-tier"
AWS_REGION="us-east-1"  # Make sure this matches your deployment region
DB_STACK_NAME="${APP_NAME}-db"

echo "==== AWS Free Tier Resources Cleanup for RoomieMatcher ===="
echo "This script will remove ALL RoomieMatcher resources from your AWS account."
echo "WARNING: This action cannot be undone and will result in data loss!"
read -p "Are you sure you want to proceed? (y/n): " -n 1 -r
echo

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cleanup cancelled."
    exit 0
fi

# Get AWS account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)
echo "AWS Account ID: $ACCOUNT_ID"

# 1. Terminate Elastic Beanstalk environment
echo "Step 1: Terminating Elastic Beanstalk environment..."
aws elasticbeanstalk describe-environments --environment-names $ENV_NAME --query "Environments[0].Status" --output text | grep -q "Terminated" || \
  aws elasticbeanstalk terminate-environment --environment-name $ENV_NAME --force-terminate

echo "Waiting for environment to terminate (this may take several minutes)..."
while aws elasticbeanstalk describe-environments --environment-names $ENV_NAME --query "Environments[0].Status" --output text 2>/dev/null | grep -q -v "Terminated"; do
  echo -n "."
  sleep 10
done
echo "Environment terminated."

# 2. Delete Elastic Beanstalk application
echo -e "\nStep 2: Deleting Elastic Beanstalk application..."
aws elasticbeanstalk delete-application --application-name $APP_NAME --terminate-env-by-force

# 3. Delete ECR repositories
echo -e "\nStep 3: Deleting ECR repositories..."
for service in api-gateway auth-service profile-service match-service notification-service; do
    repo_name="roomiematcher-${service}"
    echo "Deleting repository $repo_name..."
    aws ecr delete-repository --repository-name $repo_name --force || true
done

# 4. Delete S3 bucket
S3_BUCKET="${APP_NAME}-deployments-${ACCOUNT_ID}"
echo -e "\nStep 4: Deleting S3 bucket $S3_BUCKET..."
aws s3 rb s3://$S3_BUCKET --force || true

# 5. Check for any running EC2 instances related to the project
echo -e "\nStep 5: Checking for any stray EC2 instances..."
INSTANCES=$(aws ec2 describe-instances \
  --filters "Name=tag:elasticbeanstalk:environment-name,Values=${ENV_NAME}" "Name=instance-state-name,Values=running,pending,stopping,stopped" \
  --query "Reservations[*].Instances[*].InstanceId" \
  --output text)

if [ -n "$INSTANCES" ]; then
    echo "Found EC2 instances to terminate: $INSTANCES"
    aws ec2 terminate-instances --instance-ids $INSTANCES
    echo "Waiting for instances to terminate..."
    aws ec2 wait instance-terminated --instance-ids $INSTANCES
    echo "Instances terminated."
else
    echo "No running EC2 instances found."
fi

# 6. Check for stray security groups
echo -e "\nStep 6: Checking for stray security groups..."
SG_IDS=$(aws ec2 describe-security-groups \
  --filters "Name=tag:elasticbeanstalk:environment-name,Values=${ENV_NAME}" \
  --query "SecurityGroups[*].GroupId" \
  --output text)

if [ -n "$SG_IDS" ]; then
    echo "Found security groups to delete: $SG_IDS"
    for sg in $SG_IDS; do
        aws ec2 delete-security-group --group-id $sg || echo "Could not delete security group $sg. It may be in use."
    done
else
    echo "No security groups found."
fi

# 7. Delete RDS instance using CloudFormation
echo -e "\nStep 7: Deleting RDS instance..."
if aws cloudformation describe-stacks --stack-name $DB_STACK_NAME &>/dev/null; then
    aws cloudformation delete-stack --stack-name $DB_STACK_NAME
    echo "Waiting for RDS instance to be deleted (this may take 5-10 minutes)..."
    aws cloudformation wait stack-delete-complete --stack-name $DB_STACK_NAME
    echo "RDS instance deleted."
else
    echo "No RDS CloudFormation stack found with name $DB_STACK_NAME."
fi

# 8. Clean up SES identities
echo -e "\nStep 8: Cleaning up SES identities..."
SES_EMAIL=$(grep "AWS SES From Email:" "${APP_NAME}-credentials.txt" 2>/dev/null | cut -d' ' -f5 || echo "")

if [ -n "$SES_EMAIL" ]; then
    echo "Removing SES identity: $SES_EMAIL"
    aws ses delete-identity --identity $SES_EMAIL --region $AWS_REGION || true
else
    echo "No SES email found in credentials file. Skipping SES cleanup."
fi

# 9. Clean up IAM resources
echo -e "\nStep 9: Cleaning up IAM resources..."
# Delete SES IAM user
if aws iam get-user --user-name ${APP_NAME}-ses-user &>/dev/null; then
    echo "Removing access keys for ${APP_NAME}-ses-user..."
    ACCESS_KEYS=$(aws iam list-access-keys --user-name ${APP_NAME}-ses-user --query "AccessKeyMetadata[*].AccessKeyId" --output text)
    for key in $ACCESS_KEYS; do
        aws iam delete-access-key --user-name ${APP_NAME}-ses-user --access-key-id $key
    done
    
    echo "Detaching policies from ${APP_NAME}-ses-user..."
    POLICIES=$(aws iam list-attached-user-policies --user-name ${APP_NAME}-ses-user --query "AttachedPolicies[*].PolicyArn" --output text)
    for policy in $POLICIES; do
        aws iam detach-user-policy --user-name ${APP_NAME}-ses-user --policy-arn $policy
    done
    
    echo "Deleting IAM user ${APP_NAME}-ses-user..."
    aws iam delete-user --user-name ${APP_NAME}-ses-user
    echo "IAM user deleted."
else
    echo "No IAM user found for SES."
fi

echo -e "\nCleanup complete! All RoomieMatcher resources have been removed from your AWS account."
echo "You may want to check the AWS Console to confirm all resources have been deleted successfully."
echo "Don't forget to check AWS Billing to ensure no unexpected charges appear." 
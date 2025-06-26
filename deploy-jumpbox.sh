#!/bin/bash

set -e

# Configuration
APP_NAME="roomiematcher"
AWS_REGION="ap-south-1"
JUMPBOX_STACK_NAME="${APP_NAME}-jumpbox"
DB_STACK_NAME="${APP_NAME}-db"

echo "==== Deploying RoomieMatcher Jumpbox ===="
echo "This script will deploy a jumpbox EC2 instance to access the RDS database."

# Check for AWS CLI
if ! command -v aws &> /dev/null; then
    echo "AWS CLI not found. Please install it first."
    exit 1
fi

# Check AWS account and permissions
echo "Checking AWS account..."
aws sts get-caller-identity || { echo "Failed to authenticate with AWS. Please check your credentials."; exit 1; }

# Get VPC ID where RDS is deployed
echo "Getting VPC information..."
VPC_ID=$(aws cloudformation describe-stacks --stack-name $DB_STACK_NAME --query "Stacks[0].Parameters[?ParameterKey=='VpcId'].ParameterValue" --output text --region $AWS_REGION)
echo "Using VPC: $VPC_ID"

# Get a public subnet in the VPC
echo "Getting subnet information..."
SUBNET_ID=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPC_ID" "Name=map-public-ip-on-launch,Values=true" --query "Subnets[0].SubnetId" --output text --region $AWS_REGION)
echo "Using subnet: $SUBNET_ID"

# Get RDS security group ID
echo "Getting RDS security group..."
DB_SG_ID=$(aws cloudformation describe-stacks --stack-name $DB_STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='DBSecurityGroupId'].OutputValue" --output text --region $AWS_REGION)
echo "RDS Security Group ID: $DB_SG_ID"

# Get current IP
MY_IP=$(curl -s https://checkip.amazonaws.com)
echo "Your public IP is: $MY_IP"

# Create EC2 key pair if it doesn't exist
KEY_NAME="roomiematcher-jumpbox-key"
echo "Creating key pair $KEY_NAME if it doesn't exist..."
aws ec2 describe-key-pairs --key-names $KEY_NAME --region $AWS_REGION &>/dev/null || \
aws ec2 create-key-pair --key-name $KEY_NAME --query "KeyMaterial" --output text --region $AWS_REGION > ${KEY_NAME}.pem
chmod 400 ${KEY_NAME}.pem

# Deploy jumpbox
echo "Deploying jumpbox..."
aws cloudformation create-stack \
    --stack-name $JUMPBOX_STACK_NAME \
    --template-body file://deployment/cloudformation/database/jumpbox.yaml \
    --parameters \
        ParameterKey=VpcId,ParameterValue=$VPC_ID \
        ParameterKey=SubnetId,ParameterValue=$SUBNET_ID \
        ParameterKey=KeyName,ParameterValue=$KEY_NAME \
        ParameterKey=SourceIp,ParameterValue=${MY_IP}/32 \
        ParameterKey=DBSecurityGroupId,ParameterValue=$DB_SG_ID \
    --region $AWS_REGION

echo "Waiting for jumpbox to be created (this may take 3-5 minutes)..."
aws cloudformation wait stack-create-complete --stack-name $JUMPBOX_STACK_NAME --region $AWS_REGION

# Get jumpbox IP
JUMPBOX_IP=$(aws cloudformation describe-stacks --stack-name $JUMPBOX_STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='JumpboxPublicIP'].OutputValue" --output text --region $AWS_REGION)
RDS_ENDPOINT=$(aws cloudformation describe-stacks --stack-name $DB_STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" --output text --region $AWS_REGION)

echo "==== Jumpbox Deployment Complete ===="
echo "Jumpbox IP: $JUMPBOX_IP"
echo "RDS Endpoint: $RDS_ENDPOINT"
echo ""
echo "To connect to the jumpbox:"
echo "ssh -i ${KEY_NAME}.pem ec2-user@${JUMPBOX_IP}"
echo ""
echo "Once connected to the jumpbox, you can access the database with:"
echo "psql -h $RDS_ENDPOINT -U postgres -d postgres"
echo ""
echo "IMPORTANT: Remember to delete the jumpbox stack when you're done to avoid unnecessary charges:"
echo "aws cloudformation delete-stack --stack-name $JUMPBOX_STACK_NAME --region $AWS_REGION" 
#!/bin/bash

set -e

# Configuration
APP_NAME="roomiematcher"
ENV_NAME="free-tier"
AWS_REGION="ap-south-1"  # Updated to match the user's region
SOLUTION_STACK="64bit Amazon Linux 2 v3.5.3 running Docker"
DB_STACK_NAME="${APP_NAME}-db"

echo "==== AWS Free Tier Deployment for RoomieMatcher with RDS and SES ===="
echo "This script will deploy RoomieMatcher to AWS Elastic Beanstalk using free tier resources."

# Check for AWS CLI
if ! command -v aws &> /dev/null; then
    echo "AWS CLI not found. Please install it first."
    exit 1
fi

# Check AWS account and permissions
echo "Checking AWS account..."
aws sts get-caller-identity || { echo "Failed to authenticate with AWS. Please check your credentials."; exit 1; }
ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)

# Get VPC and Subnet information
echo "Getting VPC information..."
DEFAULT_VPC_ID=$(aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query "Vpcs[0].VpcId" --output text --region $AWS_REGION)
if [ -z "$DEFAULT_VPC_ID" ] || [ "$DEFAULT_VPC_ID" == "None" ]; then
    echo "No default VPC found. Please specify a VPC ID:"
    read -p "VPC ID: " VPC_ID
else
    VPC_ID=$DEFAULT_VPC_ID
    echo "Using default VPC: $VPC_ID"
fi

# Get public subnets
echo "Getting subnet information..."
SUBNET_IDS=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPC_ID" "Name=map-public-ip-on-launch,Values=true" --query "Subnets[*].SubnetId" --output text --region $AWS_REGION | tr '\t' ',')
if [ -z "$SUBNET_IDS" ] || [ "$SUBNET_IDS" == "None" ]; then
    echo "No public subnets found. Please specify at least two subnet IDs (comma-separated):"
    read -p "Subnet IDs: " SUBNET_IDS
else
    echo "Using public subnets: $SUBNET_IDS"
fi

# Generate secure passwords
DB_PASSWORD=$(openssl rand -base64 12)
JWT_SECRET=$(openssl rand -base64 32)

# 1. Create RDS instance if it doesn't exist
echo "Step 1: Creating RDS instance..."
aws cloudformation describe-stacks --stack-name $DB_STACK_NAME --region $AWS_REGION &>/dev/null || \
aws cloudformation create-stack \
    --stack-name $DB_STACK_NAME \
    --template-body file://deployment/cloudformation/database/rds-free-tier.yaml \
    --parameters \
        ParameterKey=VpcId,ParameterValue=$VPC_ID \
        ParameterKey=SubnetIds,ParameterValue=\"$SUBNET_IDS\" \
        ParameterKey=DBUsername,ParameterValue=postgres \
        ParameterKey=DBPassword,ParameterValue=$DB_PASSWORD \
        ParameterKey=Environment,ParameterValue=dev \
    --region $AWS_REGION

echo "Waiting for RDS instance to be created (this may take 5-10 minutes)..."
aws cloudformation wait stack-create-complete --stack-name $DB_STACK_NAME --region $AWS_REGION || true

# Get RDS endpoint
RDS_ENDPOINT=$(aws cloudformation describe-stacks --stack-name $DB_STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" --output text --region $AWS_REGION)
echo "RDS Endpoint: $RDS_ENDPOINT"

# Get security group ID
DB_SG_ID=$(aws cloudformation describe-stacks --stack-name $DB_STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='DBSecurityGroupId'].OutputValue" --output text --region $AWS_REGION)
echo "RDS Security Group ID: $DB_SG_ID"

# Add specific rule for current IP
MY_IP=$(curl -s https://checkip.amazonaws.com)
echo "Your public IP is: $MY_IP"
echo "Adding rule to allow access from your IP..."
aws ec2 authorize-security-group-ingress --group-id $DB_SG_ID --protocol tcp --port 5432 --cidr ${MY_IP}/32 --region $AWS_REGION || echo "Rule may already exist or couldn't be added. Continuing anyway."

# Wait for RDS to be fully available
echo "Waiting for RDS to become fully available..."
sleep 30

# Test connectivity
echo "Testing connectivity to RDS..."
MAX_RETRIES=5
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if PGPASSWORD=$DB_PASSWORD psql -h $RDS_ENDPOINT -p 5432 -U postgres -c "SELECT 1;" postgres &>/dev/null; then
        echo "Successfully connected to RDS!"
        break
    else
        RETRY_COUNT=$((RETRY_COUNT+1))
        if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
            echo "Failed to connect to RDS after $MAX_RETRIES attempts."
            echo "This could be due to network restrictions, security group issues, or RDS not being fully available yet."
            echo "Please check your VPC settings, security groups, and network ACLs."
            echo "Would you like to continue with deployment anyway? (y/n)"
            read -p "> " CONTINUE
            if [[ $CONTINUE != "y" && $CONTINUE != "Y" ]]; then
                echo "Deployment aborted."
                exit 1
            fi
        else
            echo "Connection attempt $RETRY_COUNT failed. Waiting 30 seconds before retrying..."
            sleep 30
        fi
    fi
done

# 2. Set up AWS SES
echo "Step 2: Configuring AWS SES..."
echo "You need to have a verified email address in AWS SES."
read -p "Enter your verified email address for AWS SES (or press Enter to use no-reply@example.com): " SES_EMAIL
SES_EMAIL=${SES_EMAIL:-"no-reply@example.com"}

# Create AWS SES credentials
echo "Creating AWS SES credentials..."
AWS_SES_ACCESS_KEY=$(aws iam create-access-key --user-name $APP_NAME-ses-user --query "AccessKey.AccessKeyId" --output text 2>/dev/null || \
    aws iam create-user --user-name $APP_NAME-ses-user && \
    aws iam attach-user-policy --user-name $APP_NAME-ses-user --policy-arn arn:aws:iam::aws:policy/AmazonSESFullAccess && \
    aws iam create-access-key --user-name $APP_NAME-ses-user --query "AccessKey.AccessKeyId" --output text)

AWS_SES_SECRET_KEY=$(aws iam list-access-keys --user-name $APP_NAME-ses-user --query "AccessKeyMetadata[0].AccessKeyId" --output text | \
    xargs -I{} aws iam get-access-key-last-used --access-key-id {} --query "AccessKey.SecretAccessKey" --output text 2>/dev/null || \
    echo "Please check IAM console for the secret key of the access key ID: $AWS_SES_ACCESS_KEY")

if [ -z "$AWS_SES_SECRET_KEY" ] || [ "$AWS_SES_SECRET_KEY" == "None" ]; then
    echo "Could not retrieve SES secret key automatically."
    echo "Please check IAM console for the secret key of access key ID: $AWS_SES_ACCESS_KEY"
    read -p "Enter the SES Secret Key: " AWS_SES_SECRET_KEY
fi

# Verify email address if needed
aws ses verify-email-identity --email-address $SES_EMAIL --region $AWS_REGION || true
echo "If this is the first time using this email with SES, please check your inbox for a verification email."

# 3. Initialize databases on RDS
echo "Step 3: Initializing databases on RDS..."
chmod +x init-db/setup-rds-databases.sh
cd init-db
./setup-rds-databases.sh $RDS_ENDPOINT postgres $DB_PASSWORD
cd ..

# 4. Create ECR repositories if they don't exist
echo "Step 4: Setting up ECR repositories..."
for service in api-gateway auth-service profile-service match-service notification-service; do
    repo_name="roomiematcher-${service}"
    aws ecr describe-repositories --repository-names $repo_name 2>/dev/null || aws ecr create-repository --repository-name $repo_name
done

# 5. Build and tag Docker images
echo "Step 5: Building Docker images..."

# Build services
mvn clean package -DskipTests

# Authenticate with ECR
echo "Logging in to ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

# Build and push service images
for service in api-gateway auth-service profile-service match-service notification-service; do
    echo "Building and pushing ${service}..."
    cd ${service}
    docker build -t ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/roomiematcher-${service}:latest .
    docker push ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/roomiematcher-${service}:latest
    cd ..
done

# 6. Update Dockerrun.aws.json with account ID and region
echo "Step 6: Preparing deployment files..."
sed -e "s/\${AWS_ACCOUNT_ID}/$ACCOUNT_ID/g" \
    -e "s/\${AWS_REGION}/$AWS_REGION/g" \
    -e "s/\${RDS_ENDPOINT}/$RDS_ENDPOINT/g" \
    -e "s/\${DB_USERNAME}/postgres/g" \
    -e "s/\${DB_PASSWORD}/$DB_PASSWORD/g" \
    -e "s/\${JWT_SECRET}/$JWT_SECRET/g" \
    -e "s/\${AWS_SES_ACCESS_KEY}/$AWS_SES_ACCESS_KEY/g" \
    -e "s/\${AWS_SES_SECRET_KEY}/$AWS_SES_SECRET_KEY/g" \
    -e "s/\${AWS_SES_REGION}/$AWS_REGION/g" \
    -e "s/\${AWS_SES_FROM_EMAIL}/$SES_EMAIL/g" \
    Dockerrun.aws.json > Dockerrun.aws.generated.json

# 7. Create application if it doesn't exist
aws elasticbeanstalk describe-applications --application-names $APP_NAME 2>/dev/null || \
    aws elasticbeanstalk create-application --application-name $APP_NAME --description "RoomieMatcher - Free Tier Deployment with RDS & SES"

# 8. Create application version
VERSION_LABEL="${APP_NAME}-${ENV_NAME}-$(date +%Y%m%d%H%M%S)"
echo "Step 7: Creating application version $VERSION_LABEL..."

# Create S3 bucket if needed
S3_BUCKET="${APP_NAME}-deployments-${ACCOUNT_ID}"
aws s3api head-bucket --bucket $S3_BUCKET 2>/dev/null || \
    aws s3 mb s3://$S3_BUCKET --region $AWS_REGION

# Create deployment bundle
zip -r deploy.zip .ebextensions Dockerrun.aws.generated.json

# Upload to S3
aws s3 cp deploy.zip s3://$S3_BUCKET/

# Create the application version
aws elasticbeanstalk create-application-version \
    --application-name $APP_NAME \
    --version-label $VERSION_LABEL \
    --source-bundle S3Bucket=$S3_BUCKET,S3Key=deploy.zip \
    --auto-create-application \
    --region $AWS_REGION

# 9. Deploy to Elastic Beanstalk
echo "Step 8: Deploying to Elastic Beanstalk..."
ENV_EXISTS=$(aws elasticbeanstalk describe-environments --application-name $APP_NAME --environment-names $ENV_NAME --query "Environments[0].EnvironmentName" --output text 2>/dev/null)

if [ "$ENV_EXISTS" == "$ENV_NAME" ]; then
    # Update existing environment
    echo "Updating existing environment $ENV_NAME..."
    aws elasticbeanstalk update-environment \
        --environment-name $ENV_NAME \
        --version-label $VERSION_LABEL \
        --option-settings file://env-config-minimal.json
else
    # Create new environment
    echo "Creating new environment $ENV_NAME..."
    aws elasticbeanstalk create-environment \
        --application-name $APP_NAME \
        --environment-name $ENV_NAME \
        --solution-stack-name "$SOLUTION_STACK" \
        --option-settings file://env-config-minimal.json \
        --version-label $VERSION_LABEL \
        --tier "Name=WebServer,Type=Standard,Version=''" \
        --region $AWS_REGION
fi

# 10. Save credentials for future reference (securely)
echo "Step 9: Saving deployment credentials securely to ${APP_NAME}-credentials.txt..."
{
    echo "Application Name: $APP_NAME"
    echo "Environment Name: $ENV_NAME"
    echo "RDS Endpoint: $RDS_ENDPOINT"
    echo "Database Username: postgres"
    echo "Database Password: $DB_PASSWORD"
    echo "JWT Secret: $JWT_SECRET"
    echo "AWS SES Access Key: $AWS_SES_ACCESS_KEY"
    echo "AWS SES Secret Key: $AWS_SES_SECRET_KEY"
    echo "AWS SES From Email: $SES_EMAIL"
    echo "AWS Account ID: $ACCOUNT_ID"
    echo "AWS Region: $AWS_REGION"
    echo "Deployment Date: $(date)"
} > ${APP_NAME}-credentials.txt
chmod 600 ${APP_NAME}-credentials.txt

echo "==== Deployment initiated ===="
echo "Your application is being deployed to Elastic Beanstalk."
echo "To check deployment status: aws elasticbeanstalk describe-environments --environment-names $ENV_NAME"
echo "After deployment completes, your application will be available at: http://$ENV_NAME.${AWS_REGION}.elasticbeanstalk.com"
echo "IMPORTANT: Keep ${APP_NAME}-credentials.txt file secure as it contains sensitive information."
echo "IMPORTANT: This deployment uses the AWS Free Tier resources. Monitor your usage to avoid charges." 
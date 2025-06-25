#!/bin/bash

# RoomieMatcher AWS Free Tier Usage Monitor
# This script helps track usage to ensure you stay within Free Tier limits

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Default region
DEFAULT_REGION="us-east-1"

# Get region from command line or use default
if [ -n "$1" ]; then
  AWS_REGION="$1"
else
  # Try to get region from AWS config
  CONFIG_REGION=$(aws configure get region 2>/dev/null)
  if [ -n "$CONFIG_REGION" ]; then
    AWS_REGION="$CONFIG_REGION"
  else
    AWS_REGION="$DEFAULT_REGION"
  fi
fi

echo -e "${GREEN}=== RoomieMatcher AWS Free Tier Usage Monitor ===${NC}"
echo "Running checks for Free Tier eligible resources in region: $AWS_REGION"

# Check AWS CLI availability
if ! command -v aws &> /dev/null; then
    echo -e "${RED}Error: AWS CLI is not installed or not in PATH${NC}"
    echo "Please install AWS CLI: https://aws.amazon.com/cli/"
    exit 1
fi

# Check AWS credentials
echo "Checking AWS credentials..."
if ! aws sts get-caller-identity &>/dev/null; then
    echo -e "${RED}Error: AWS credentials not configured or invalid${NC}"
    echo "Please run 'aws configure' to set up your credentials"
    exit 1
fi

# Get AWS account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)
echo -e "${GREEN}AWS Account ID:${NC} $ACCOUNT_ID"

# Check EC2 usage
echo -e "\n${GREEN}== EC2 Usage ==${NC}"
echo "Free Tier: 750 hours of t2.micro per month"

# List running instances
echo "Fetching EC2 instances..."
INSTANCES=$(aws ec2 describe-instances \
  --region "$AWS_REGION" \
  --filters "Name=instance-state-name,Values=running" \
  --query 'Reservations[*].Instances[*].[InstanceId,InstanceType,LaunchTime]' \
  --output text 2>/dev/null || echo "ERROR")

if [ "$INSTANCES" = "ERROR" ]; then
  echo -e "${YELLOW}Failed to retrieve EC2 instances. Check permissions.${NC}"
elif [ -z "$INSTANCES" ]; then
  echo -e "${YELLOW}No running instances found${NC}"
else
  echo -e "${GREEN}Running Instances:${NC}"
  FREE_TIER_HOURS=0
  NON_FREE_TIER_INSTANCES=0
  
  echo "$INSTANCES" | while read -r id type launch; do
    # Calculate hours used
    LAUNCH_TIME=$(date -d "$launch" +%s 2>/dev/null || date -j -f "%Y-%m-%dT%H:%M:%S.000Z" "$launch" "+%s" 2>/dev/null)
    if [ $? -ne 0 ]; then
      HOURS="Unknown"
      TIER_STATUS="${YELLOW}Unknown${NC}"
    else
      CURRENT_TIME=$(date +%s)
      HOURS=$(( (CURRENT_TIME - LAUNCH_TIME) / 3600 ))
      
      if [[ "$type" == "t2.micro" ]]; then
        TIER_STATUS="${GREEN}Free Tier Eligible${NC}"
        FREE_TIER_HOURS=$((FREE_TIER_HOURS + HOURS))
      else
        TIER_STATUS="${RED}NOT Free Tier${NC}"
        NON_FREE_TIER_INSTANCES=$((NON_FREE_TIER_INSTANCES + 1))
      fi
    fi
    
    echo -e "ID: $id, Type: $type, Hours: $HOURS, Status: $TIER_STATUS"
  done
  
  if [ $FREE_TIER_HOURS -gt 0 ]; then
    echo -e "\nTotal Free Tier hours used: $FREE_TIER_HOURS (Limit: 750 hours/month)"
    if [ $FREE_TIER_HOURS -gt 700 ]; then
      echo -e "${RED}Warning: Approaching Free Tier limit!${NC}"
    fi
  fi
  
  if [ $NON_FREE_TIER_INSTANCES -gt 0 ]; then
    echo -e "${RED}Warning: $NON_FREE_TIER_INSTANCES instances are not Free Tier eligible!${NC}"
  fi
fi

# Check Elastic Beanstalk environments
echo -e "\n${GREEN}== Elastic Beanstalk Environments ==${NC}"
echo "Fetching Elastic Beanstalk environments..."
ENV_LIST=$(aws elasticbeanstalk describe-environments \
  --region "$AWS_REGION" \
  --query 'Environments[*].[EnvironmentName,Status,Health,EnvironmentId]' \
  --output text 2>/dev/null || echo "ERROR")

if [ "$ENV_LIST" = "ERROR" ]; then
  echo -e "${YELLOW}Failed to retrieve Elastic Beanstalk environments. Check permissions.${NC}"
elif [ -z "$ENV_LIST" ]; then
  echo -e "${YELLOW}No Elastic Beanstalk environments found${NC}"
else
  echo "$ENV_LIST" | while read -r name status health id; do
    echo -e "Name: $name, Status: $status, Health: $health"
    
    # Check if environment is using load balancer (not free tier)
    ENV_INFO=$(aws elasticbeanstalk describe-configuration-settings \
      --region "$AWS_REGION" \
      --environment-name "$name" \
      --application-name "roomiematcher" 2>/dev/null)
    
    if echo "$ENV_INFO" | grep -q "LoadBalancerType.*application"; then
      echo -e "  ${RED}Warning: Using Application Load Balancer (not Free Tier)${NC}"
    elif echo "$ENV_INFO" | grep -q "EnvironmentType.*LoadBalanced"; then
      echo -e "  ${RED}Warning: Using Load Balanced environment (not Free Tier)${NC}"
    fi
  done
fi

# Check RDS instances
echo -e "\n${GREEN}== RDS Instances ==${NC}"
echo "Free Tier: 750 hours/month of db.t2.micro or db.t3.micro"

echo "Fetching RDS instances..."
RDS_INSTANCES=$(aws rds describe-db-instances \
  --region "$AWS_REGION" \
  --query 'DBInstances[*].[DBInstanceIdentifier,DBInstanceClass,Engine,DBInstanceStatus]' \
  --output text 2>/dev/null || echo "ERROR")

if [ "$RDS_INSTANCES" = "ERROR" ]; then
  echo -e "${YELLOW}Failed to retrieve RDS instances. Check permissions.${NC}"
elif [ -z "$RDS_INSTANCES" ]; then
  echo -e "${YELLOW}No RDS instances found${NC}"
else
  echo -e "${GREEN}Running RDS Instances:${NC}"
  TOTAL_NON_FREE=0
  
  echo "$RDS_INSTANCES" | while read -r id class engine status; do
    # Check if instance is free tier eligible
    if [[ "$class" == "db.t2.micro" || "$class" == "db.t3.micro" ]]; then
      TIER_STATUS="${GREEN}Free Tier Eligible${NC}"
    else
      TIER_STATUS="${RED}NOT Free Tier${NC}"
      ((TOTAL_NON_FREE++))
    fi
    
    echo -e "ID: $id, Class: $class, Engine: $engine, Status: $status, Free Tier: $TIER_STATUS"
    
    # Get storage information
    STORAGE=$(aws rds describe-db-instances \
      --region "$AWS_REGION" \
      --db-instance-identifier "$id" \
      --query 'DBInstances[0].AllocatedStorage' \
      --output text 2>/dev/null || echo "Unknown")
    
    echo -e "  - Allocated Storage: ${STORAGE}GB"
    
    # Check for Multi-AZ (not free tier)
    MULTI_AZ=$(aws rds describe-db-instances \
      --region "$AWS_REGION" \
      --db-instance-identifier "$id" \
      --query 'DBInstances[0].MultiAZ' \
      --output text 2>/dev/null || echo "Unknown")
    
    if [ "$MULTI_AZ" = "True" ]; then
      echo -e "  ${RED}Warning: Multi-AZ deployment (not Free Tier)${NC}"
    fi
  done
  
  if [ "$TOTAL_NON_FREE" -gt 0 ]; then
    echo -e "\n${RED}Warning: $TOTAL_NON_FREE RDS instances are not Free Tier eligible!${NC}"
  fi
fi

# Check AWS SES usage
echo -e "\n${GREEN}== AWS SES Usage ==${NC}"
echo "Free Tier: 62,000 outbound messages/month from EC2"

# Check if SES is set up
echo "Fetching SES identities..."
SES_IDENTITIES=$(aws ses list-identities --region "$AWS_REGION" --output text 2>/dev/null || echo "ERROR")

if [ "$SES_IDENTITIES" = "ERROR" ]; then
  echo -e "${YELLOW}Failed to retrieve SES identities. Check permissions or if SES is available in $AWS_REGION.${NC}"
elif [ -z "$SES_IDENTITIES" ]; then
  echo -e "${YELLOW}No SES identities found${NC}"
else
  echo -e "${GREEN}Verified Email Identities:${NC}"
  VERIFIED_EMAILS=$(aws ses list-identities --identity-type EmailAddress --region "$AWS_REGION" --query 'Identities' --output text)
  
  if [ -n "$VERIFIED_EMAILS" ]; then
    for email in $VERIFIED_EMAILS; do
      VERIFICATION_STATUS=$(aws ses get-identity-verification-attributes \
        --region "$AWS_REGION" \
        --identities "$email" \
        --query "VerificationAttributes.\"$email\".VerificationStatus" \
        --output text 2>/dev/null || echo "Unknown")
      
      if [ "$VERIFICATION_STATUS" = "Success" ]; then
        echo -e "$email: ${GREEN}Verified${NC}"
      else
        echo -e "$email: ${YELLOW}$VERIFICATION_STATUS${NC}"
      fi
    done
  else
    echo -e "${YELLOW}No email identities found${NC}"
  fi
  
  # Check if in sandbox mode
  SENDING_ENABLED=$(aws ses get-account-sending-enabled --region "$AWS_REGION" --query 'Enabled' --output text 2>/dev/null || echo "Unknown")
  SEND_QUOTA=$(aws ses get-send-quota --region "$AWS_REGION" --query 'Max24HourSend' --output text 2>/dev/null || echo "Unknown")
  
  if [ "$SEND_QUOTA" != "Unknown" ] && [ $(echo "$SEND_QUOTA < 200" | bc -l) -eq 1 ]; then
    echo -e "\n${YELLOW}SES account is in SANDBOX mode. Email can only be sent to verified addresses.${NC}"
    echo "Request production access here: https://docs.aws.amazon.com/ses/latest/dg/request-production-access.html"
  fi
  
  if [ "$SEND_QUOTA" != "Unknown" ]; then
    echo -e "\nSend quota: $SEND_QUOTA emails per 24 hours"
  fi
fi

# Check ECR storage
echo -e "\n${GREEN}== ECR Storage ==${NC}"
echo "Fetching ECR repositories..."
REPOS=$(aws ecr describe-repositories \
  --region "$AWS_REGION" \
  --query 'repositories[*].[repositoryName,repositoryUri]' \
  --output text 2>/dev/null || echo "ERROR")

if [ "$REPOS" = "ERROR" ]; then
  echo -e "${YELLOW}Failed to retrieve ECR repositories. Check permissions.${NC}"
elif [ -z "$REPOS" ]; then
  echo -e "${YELLOW}No ECR repositories found${NC}"
else
  TOTAL_SIZE_MB=0
  
  echo "$REPOS" | while read -r name uri; do
    echo -e "Repository: $name"
    
    # Get image counts
    IMAGE_COUNT=$(aws ecr describe-images --region "$AWS_REGION" --repository-name "$name" --query 'length(imageDetails)' 2>/dev/null || echo "0")
    echo "Image count: $IMAGE_COUNT"
    
    # Calculate approximate size (not exact)
    if [ "$IMAGE_COUNT" -gt 0 ]; then
      IMAGES=$(aws ecr describe-images --region "$AWS_REGION" --repository-name "$name" --query 'imageDetails[*].[imageSizeInBytes]' --output text 2>/dev/null || echo "0")
      if command -v bc &> /dev/null; then
        SIZE_BYTES=$(echo "$IMAGES" | paste -sd+ - | bc)
        SIZE_MB=$(echo "scale=2; $SIZE_BYTES/1048576" | bc)
      else
        SIZE_MB="Unknown (bc command not available)"
      fi
      echo -e "Approximate size: ${SIZE_MB}MB"
      
      if [[ "$SIZE_MB" != Unknown* ]]; then
        TOTAL_SIZE_MB=$(echo "$TOTAL_SIZE_MB + $SIZE_MB" | bc)
      fi
    fi
  done
  
  echo -e "\nTotal ECR Storage: Approximately ${TOTAL_SIZE_MB}MB"
  echo "Free Tier: First 500MB per month free"
  
  if command -v bc &> /dev/null && (( $(echo "$TOTAL_SIZE_MB > 500" | bc -l) )); then
    echo -e "${RED}Warning: ECR storage exceeds Free Tier limit!${NC}"
  fi
fi

# Check for load balancers (should be none)
echo -e "\n${GREEN}== Load Balancers ==${NC}"
echo "Free Tier Deployment should NOT have load balancers."

echo "Checking for load balancers..."
LBS=$(aws elbv2 describe-load-balancers \
  --region "$AWS_REGION" \
  --query 'LoadBalancers[*].[LoadBalancerName,DNSName]' \
  --output text 2>/dev/null || echo "ERROR")

if [ "$LBS" = "ERROR" ]; then
  echo -e "${YELLOW}Failed to retrieve load balancers. Check permissions.${NC}"
elif [ -z "$LBS" ]; then
  echo -e "${GREEN}No load balancers found - Good!${NC}"
else
  echo -e "${RED}Warning: Load Balancers found. These will incur charges!${NC}"
  echo "$LBS"
fi

# Check current month's billing estimate
echo -e "\n${GREEN}== Current Month Billing ==${NC}"
echo "Getting estimated AWS charges..."
CURRENT_MONTH=$(date +%Y-%m)
COST_DATA=$(aws ce get-cost-and-usage \
  --time-period Start=${CURRENT_MONTH}-01,End=$(date -d "next month" +%Y-%m-01 2>/dev/null || date -v+1m -f "%Y-%m-01" "${CURRENT_MONTH}-01" 2>/dev/null) \
  --granularity MONTHLY \
  --metrics "UnblendedCost" 2>/dev/null || echo "ERROR")

if [ "$COST_DATA" = "ERROR" ]; then
  echo -e "${YELLOW}Cost Explorer API not enabled or insufficient permissions.${NC}"
  echo -e "Enable Cost Explorer in the AWS Billing console to track costs."
else
  TOTAL_COST=$(echo "$COST_DATA" | grep -o '"Amount": "[^"]*' | cut -d'"' -f4)
  UNIT=$(echo "$COST_DATA" | grep -o '"Unit": "[^"]*' | cut -d'"' -f4)
  
  if [ -n "$TOTAL_COST" ]; then
    echo -e "Estimated charges for current month: $TOTAL_COST $UNIT"
    
    if command -v bc &> /dev/null && (( $(echo "$TOTAL_COST > 0.5" | bc -l) )); then
      echo -e "${YELLOW}Warning: Current charges might exceed expected Free Tier usage.${NC}"
    fi
  else
    echo -e "${YELLOW}Could not retrieve cost data.${NC}"
  fi
fi

# Final recommendations
echo -e "\n${GREEN}== Recommendations ==${NC}"
echo -e "1. Ensure you're using only ${GREEN}t2.micro${NC} EC2 instances"
echo -e "2. Ensure you're using only ${GREEN}db.t2.micro${NC} RDS instances"
echo -e "3. Verify that you have ${RED}NO${NC} load balancers"
echo -e "4. Monitor AWS Billing dashboard regularly"
echo -e "5. Set up budget alerts in AWS Budget"

echo -e "\n${GREEN}== Estimated Free Tier Status ==${NC}"
if [ "$INSTANCES" = "ERROR" ] || [ "$RDS_INSTANCES" = "ERROR" ] || [ "$LBS" = "ERROR" ]; then
  echo -e "${YELLOW}Could not determine Free Tier status due to API errors${NC}"
elif [ -z "$INSTANCES" ] && [ -z "$RDS_INSTANCES" ] && [ -z "$LBS" ]; then
  echo -e "${GREEN}✓ No resources found that would incur charges${NC}"
elif [[ "$INSTANCES" == *"t2.micro"* ]] && [[ "$RDS_INSTANCES" == *"db.t2.micro"* || "$RDS_INSTANCES" == *"db.t3.micro"* ]] && [ -z "$LBS" ]; then
  echo -e "${GREEN}✓ Current deployment appears to be within Free Tier limits${NC}"
else
  echo -e "${RED}⚠ Current deployment may EXCEED Free Tier limits!${NC}"
  echo -e "Run AWS Cost Explorer for detailed cost analysis."
fi

echo -e "\nFor more details on AWS Free Tier: https://aws.amazon.com/free/"
echo -e "To create budget alerts: https://aws.amazon.com/getting-started/hands-on/control-your-costs-free-tier-budgets/" 
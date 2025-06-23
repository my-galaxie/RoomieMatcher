# RoomieMatcher Deployment Secrets Setup

This document provides instructions on setting up the required secrets in your GitHub repository for deploying the RoomieMatcher application to AWS Elastic Beanstalk.

## Required GitHub Secrets

To successfully deploy the application, you need to add the following secrets to your GitHub repository:

### AWS Credentials

1. **AWS_ACCESS_KEY_ID**: Your AWS access key ID
2. **AWS_SECRET_ACCESS_KEY**: Your AWS secret access key
3. **AWS_REGION**: The AWS region where your resources are located (e.g., `us-east-1`)
4. **AWS_ACCOUNT_ID**: Your AWS account ID

### Database Credentials

5. **RDS_ENDPOINT**: Your RDS instance endpoint (e.g., `your-db-instance.rds.amazonaws.com:5432`)
6. **MASTER_DB_USERNAME**: Master database username (e.g., `postgres`)
7. **MASTER_DB_PASSWORD**: Master database password

#### Service-specific Database Credentials

8. **AUTH_DB_USERNAME**: Auth service database username (e.g., `auth_user`)
9. **AUTH_DB_PASSWORD**: Auth service database password
10. **PROFILE_DB_USERNAME**: Profile service database username (e.g., `profile_user`)
11. **PROFILE_DB_PASSWORD**: Profile service database password
12. **MATCH_DB_USERNAME**: Match service database username (e.g., `match_user`)
13. **MATCH_DB_PASSWORD**: Match service database password
14. **NOTIFICATION_DB_USERNAME**: Notification service database username (e.g., `notification_user`)
15. **NOTIFICATION_DB_PASSWORD**: Notification service database password

### JWT Configuration

16. **JWT_SECRET**: Secret key for JWT token signing (generate a strong random string)

### Email Configuration

#### Standard Email (SMTP)

17. **MAIL_HOST**: SMTP server host (e.g., `smtp.gmail.com`)
18. **MAIL_PORT**: SMTP server port (e.g., `587`)
19. **MAIL_USERNAME**: Email username/address
20. **MAIL_PASSWORD**: Email password or app-specific password

#### AWS SES (Simple Email Service)

21. **AWS_SES_ENABLED**: Set to `true` to enable AWS SES
22. **AWS_SES_ACCESS_KEY**: AWS SES access key (can be same as AWS_ACCESS_KEY_ID if using the same AWS account)
23. **AWS_SES_SECRET_KEY**: AWS SES secret key (can be same as AWS_SECRET_ACCESS_KEY if using the same AWS account)
24. **AWS_SES_REGION**: AWS SES region (e.g., `us-east-1`)
25. **AWS_SES_FROM_EMAIL**: Email address to send from (must be verified in AWS SES)
26. **NOTIFICATION_PROVIDER**: Set to `aws-ses` to use AWS SES or `standard` to use SMTP

## How to Add Secrets to GitHub Repository

1. Navigate to your GitHub repository
2. Click on "Settings"
3. Click on "Secrets and variables" in the left sidebar
4. Click on "Actions"
5. Click on "New repository secret"
6. Add each secret with its name and value
7. Click "Add secret"

## Generating Secure Values

### JWT Secret

Generate a secure random string for JWT_SECRET:

```bash
openssl rand -base64 32
```

### Database Passwords

Generate secure random strings for each database user:

```bash
# Generate master database password
openssl rand -base64 16

# Generate service-specific database passwords
openssl rand -base64 16  # AUTH_DB_PASSWORD
openssl rand -base64 16  # PROFILE_DB_PASSWORD
openssl rand -base64 16  # MATCH_DB_PASSWORD
openssl rand -base64 16  # NOTIFICATION_DB_PASSWORD
```

### AWS Credentials

1. Create an IAM user with programmatic access in the AWS Management Console
2. Attach the necessary policies:
   - AmazonS3FullAccess
   - AmazonECR-FullAccess
   - AWSElasticBeanstalkFullAccess
   - AmazonRDSFullAccess
3. Copy the Access Key ID and Secret Access Key

### AWS SES Setup

1. Verify your email domain or email address in AWS SES
2. If your account is in the SES sandbox, also verify recipient email addresses
3. Create an IAM user with the AmazonSESFullAccess policy or use your existing AWS credentials

## Setting Up the RDS Instance

Before deploying the application, you need to set up the RDS instance and initialize the databases:

1. Deploy the CloudFormation template for the RDS instance:
   ```bash
   aws cloudformation create-stack \
     --stack-name roomiematcher-db \
     --template-body file://deployment/cloudformation/database/rds.yaml \
     --parameters \
       ParameterKey=VpcId,ParameterValue=<your-vpc-id> \
       ParameterKey=SubnetIds,ParameterValue="<subnet-id-1>,<subnet-id-2>" \
       ParameterKey=DBUsername,ParameterValue=<master-username> \
       ParameterKey=DBPassword,ParameterValue=<master-password> \
       ParameterKey=Environment,ParameterValue=prod
   ```

2. Get the RDS endpoint:
   ```bash
   aws cloudformation describe-stacks \
     --stack-name roomiematcher-db \
     --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" \
     --output text
   ```

3. Initialize the databases using the setup script:
   ```bash
   cd deployment/scripts
   ./setup-rds-databases.sh <rds-endpoint> <master-username> <master-password>
   ```

4. Generate environment variables for your services:
   ```bash
   ./setup-env-variables.sh <rds-endpoint> env-variables.txt
   ```

5. Add the generated credentials as GitHub secrets

## Testing the Deployment

After setting up all the required secrets, push a commit to your main branch to trigger the deployment workflow. Monitor the workflow execution in the "Actions" tab of your GitHub repository. 
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

5. **DB_USERNAME**: Database username (e.g., `postgres`)
6. **DB_PASSWORD**: Database password

### JWT Configuration

7. **JWT_SECRET**: Secret key for JWT token signing (generate a strong random string)

### Email Configuration

#### Standard Email (SMTP)

8. **MAIL_HOST**: SMTP server host (e.g., `smtp.gmail.com`)
9. **MAIL_PORT**: SMTP server port (e.g., `587`)
10. **MAIL_USERNAME**: Email username/address
11. **MAIL_PASSWORD**: Email password or app-specific password

#### AWS SES (Simple Email Service)

12. **AWS_SES_ACCESS_KEY**: AWS SES access key (can be same as AWS_ACCESS_KEY_ID if using the same AWS account)
13. **AWS_SES_SECRET_KEY**: AWS SES secret key (can be same as AWS_SECRET_ACCESS_KEY if using the same AWS account)
14. **AWS_SES_REGION**: AWS SES region (e.g., `us-east-1`)
15. **AWS_SES_FROM_EMAIL**: Email address to send from (must be verified in AWS SES)
16. **NOTIFICATION_PROVIDER**: Set to `aws-ses` to use AWS SES or `standard` to use SMTP

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

### AWS Credentials

1. Create an IAM user with programmatic access in the AWS Management Console
2. Attach the necessary policies:
   - AmazonS3FullAccess
   - AmazonECR-FullAccess
   - AWSElasticBeanstalkFullAccess
3. Copy the Access Key ID and Secret Access Key

### AWS SES Setup

1. Verify your email domain or email address in AWS SES
2. If your account is in the SES sandbox, also verify recipient email addresses
3. Create an IAM user with the AmazonSESFullAccess policy or use your existing AWS credentials

## Testing the Deployment

After setting up all the required secrets, push a commit to your main branch to trigger the deployment workflow. Monitor the workflow execution in the "Actions" tab of your GitHub repository. 
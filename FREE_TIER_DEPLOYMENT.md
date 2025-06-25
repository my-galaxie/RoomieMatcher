# RoomieMatcher AWS Free Tier Deployment Guide

This guide explains how to deploy the RoomieMatcher application on AWS using only Free Tier resources.

## Free Tier Architecture Overview

1. **Single EC2 t2.micro Instance** (Free Tier: 750 hours/month)
   - All services run as Docker containers on a single EC2 instance
   - No Load Balancer (direct access to EC2 instance)
   - Elastic Beanstalk Single Instance environment

2. **RDS db.t2.micro PostgreSQL Database** (Free Tier: 750 hours/month)
   - Single PostgreSQL RDS instance with multiple databases
   - Separate databases for each microservice
   - No Multi-AZ deployment to stay within Free Tier

3. **AWS SES for Email Notifications** (Free Tier: 62,000 outbound messages/month)
   - Used for authentication emails and OTP verification
   - Configured with verified sender email

4. **Minimal Resource Usage**
   - Containers limited to 150MB memory each
   - Java heap sizes limited to 128MB
   - Low CPU utilization

## Prerequisites

- AWS account with Free Tier eligibility
- AWS CLI installed and configured
- Docker installed
- Java 17+ and Maven installed
- PostgreSQL client (psql) installed

## Deployment Options

You have two options for deployment:

### Option 1: Manual Deployment

1. **Prepare Environment**

   Make sure all prerequisites are installed. Clone this repository and navigate to its root directory.

2. **Configure Region**

   Edit `deploy-free-tier.sh` to set your preferred AWS region that supports Free Tier:

   ```bash
   AWS_REGION="us-east-1"  # Change as needed
   ```

3. **Deploy the Application**

   Run the deployment script:

   ```bash
   ./deploy-free-tier.sh
   ```

   This script will:
   - Create a db.t2.micro RDS instance with PostgreSQL
   - Set up multiple databases on the RDS instance
   - Configure AWS SES with a verified email
   - Build and push Docker images to Amazon ECR
   - Configure Elastic Beanstalk settings for Free Tier
   - Deploy the application to AWS

4. **Access the Application**

   After deployment completes (which may take 10-15 minutes), your application will be available at:
   
   ```
   http://free-tier.region.elasticbeanstalk.com
   ```

### Option 2: CI/CD Deployment

1. **Run Initial Setup Script**

   ```bash
   ./deploy-free-tier.sh
   ```

   This creates the RDS instance and configures SES.

2. **Configure GitHub Secrets**

   Add all required secrets to GitHub Actions as described in GITHUB_CICD_SETUP.md

3. **Push to Main Branch**

   The CI/CD pipeline will automatically build and deploy your application.

## AWS Services Used

| Service | Free Tier Allowance | Our Usage |
|---------|---------------------|-----------|
| EC2 | 750 hours/month of t2.micro | 1 t2.micro instance (744 hours/month) |
| RDS | 750 hours/month of db.t2.micro | 1 db.t2.micro instance (744 hours/month) |
| ECR | 500MB storage/month | ~500MB for Docker images |
| S3 | 5GB storage | <1GB for deployment artifacts |
| SES | 62,000 outbound messages/month | Used for authentication emails |
| Elastic Beanstalk | No additional charge | Used for deployment management |

## Cost-Saving Measures

The following features have been implemented to ensure the deployment stays within Free Tier limits:

1. **Single Instance Environment**
   - Eliminates Load Balancer costs (savings: ~$16/month)
   - Uses only one t2.micro instance (Free Tier eligible)

2. **Optimized RDS Usage**
   - Uses db.t2.micro instance (Free Tier eligible)
   - Shared RDS instance with multiple databases
   - No Multi-AZ deployment

3. **Memory Optimization**
   - Reduced container memory allocations
   - JVM heap size limits

4. **Spot Instances**
   - Optional: EC2 Spot Instance for further cost reduction

## Database Setup

The RDS instance is set up with four separate databases:

1. `roomie_auth` - Authentication and user management
2. `roomie_profile` - User profile information
3. `roomie_match` - Roommate matching data
4. `roomie_notification` - Notification tracking

The database initialization is handled by the `setup-rds-databases.sh` script.

## Email Services

AWS SES is used for sending:
- Registration confirmation emails
- OTP verification codes
- Password reset links

To use AWS SES, you need to verify your sender email address in the AWS SES console or through the deployment script.

## Monitoring Usage

Even with Free Tier resources, it's important to monitor your AWS usage:

1. **Set up Budget Alerts**
   ```bash
   aws budgets create-budget --account-id $(aws sts get-caller-identity --query "Account" --output text) --budget '{"BudgetName":"FreeTier","BudgetLimit":{"Amount":"1","Unit":"USD"},"TimeUnit":"MONTHLY","BudgetType":"COST"}'
   ```

2. **Check EC2 and RDS Usage**
   ```bash
   ./monitor-free-tier.sh
   ```

## Limitations

This Free Tier deployment has some limitations:

1. **Limited Scalability**
   - Single instance cannot handle high traffic
   - Limited database performance

2. **No High Availability**
   - No redundancy or failover capability

3. **Email Limitations**
   - AWS SES may initially be in sandbox mode with sending restrictions

## Upgrading Later

When you need to upgrade beyond Free Tier:

1. **Add Load Balancer**
   - Update `env-config-minimal.json` to use load balancer
   - Enable multi-container environment

2. **Upgrade RDS Instance**
   - Increase RDS instance size
   - Enable Multi-AZ deployment

3. **Move out of SES Sandbox**
   - Request production access for AWS SES

## Troubleshooting

Common issues and their solutions:

1. **Deployment Fails**
   - Check Elastic Beanstalk logs
   - Ensure AWS region supports Free Tier

2. **Database Connection Issues**
   - Check RDS security group settings
   - Verify database initialization was successful

3. **Email Not Sending**
   - Verify SES email address is confirmed
   - Check if you're still in SES sandbox mode 
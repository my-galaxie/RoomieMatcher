# RoomieMatcher: GitHub to AWS Elastic Beanstalk Deployment Guide

This guide provides detailed, step-by-step instructions for deploying the RoomieMatcher application to AWS Elastic Beanstalk using GitHub Actions CI/CD pipeline.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Initial AWS Setup](#initial-aws-setup)
3. [GitHub Repository Setup](#github-repository-setup)
4. [GitHub Actions Configuration](#github-actions-configuration)
5. [Deployment Process](#deployment-process)
6. [Monitoring and Troubleshooting](#monitoring-and-troubleshooting)
7. [Updating Your Application](#updating-your-application)
8. [Cleanup and Resource Management](#cleanup-and-resource-management)

## Prerequisites

Before starting the deployment process, ensure you have:

- [ ] An AWS account with Free Tier eligibility
- [ ] A GitHub account with the RoomieMatcher repository
- [ ] AWS CLI installed and configured locally
- [ ] Git installed locally
- [ ] Basic understanding of Docker, Java, and AWS services

## Initial AWS Setup

### Step 1: Create IAM User for Deployments

1. Log in to the AWS Management Console
2. Navigate to IAM (Identity and Access Management)
3. Click on "Users" in the left sidebar, then "Add user"
4. Enter a username (e.g., `roomiematcher-deployer`)
5. Select "Programmatic access" for Access type
6. Click "Next: Permissions"
7. Click "Attach existing policies directly"
8. Search for and select the following policies:
   - `AmazonEC2ContainerRegistryFullAccess`
   - `AWSElasticBeanstalkFullAccess`
   - `AmazonS3FullAccess`
9. Click "Next: Tags" (optional: add tags)
10. Click "Next: Review" and then "Create user"
11. **IMPORTANT**: Download or copy the Access Key ID and Secret Access Key - you'll need these for GitHub Secrets

### Step 2: Set Up AWS RDS Database

1. Run the provided script to create and configure the RDS instance:

```bash
./deploy-free-tier.sh
```

This script will:
- Create a db.t2.micro PostgreSQL RDS instance
- Configure security groups for access
- Create the required databases (roomie_auth, roomie_profile, roomie_match, roomie_notification)
- Initialize database schemas

### Step 3: Configure AWS SES for Email Notifications

1. Navigate to Amazon SES in the AWS Console
2. Go to "Email Addresses" under "Identity Management"
3. Click "Verify a New Email Address"
4. Enter the email address you want to use for sending notifications
5. Check the inbox for the verification email and click the verification link
6. Note: By default, SES will be in sandbox mode (can only send to verified email addresses)

## GitHub Repository Setup

### Step 1: Fork or Clone the Repository

If you don't already have the RoomieMatcher repository:

```bash
git clone https://github.com/your-username/roomiematcher.git
cd roomiematcher
```

### Step 2: Configure GitHub Secrets

1. In your GitHub repository, go to "Settings" > "Secrets and variables" > "Actions"
2. Click "New repository secret"
3. Add the following secrets:

| Secret Name | Description | Example Value |
|-------------|-------------|--------------|
| `AWS_ACCESS_KEY_ID` | IAM user access key | `AKIA1234567890ABCDEF` |
| `AWS_SECRET_ACCESS_KEY` | IAM user secret key | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` |
| `AWS_REGION` | AWS region for deployment | `us-east-1` |
| `RDS_ENDPOINT` | RDS instance endpoint | `roomiematcher-db.cxyz123456.us-east-1.rds.amazonaws.com` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `YourStrongPassword` |
| `JWT_SECRET` | Secret key for JWT token generation | `YourJwtSecretKey` |
| `AWS_SES_ACCESS_KEY` | AWS SES access key | `AKIA1234567890GHIJKL` |
| `AWS_SES_SECRET_KEY` | AWS SES secret key | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY2` |
| `AWS_SES_REGION` | AWS SES region | `us-east-1` |
| `AWS_SES_FROM_EMAIL` | Verified email address for sending emails | `no-reply@yourdomain.com` |

## GitHub Actions Configuration

### Step 1: Create or Update Workflow File

Ensure the `.github/workflows/deploy-to-aws.yml` file exists with the following content:

```yaml
name: Deploy to AWS Elastic Beanstalk

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn clean package -DskipTests

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v2
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ${{ secrets.AWS_REGION }}

    - name: Login to Amazon ECR
      id: login-ecr
      uses: aws-actions/amazon-ecr-login@v1

    - name: Get AWS account ID
      id: get-aws-account
      run: echo "::set-output name=account_id::$(aws sts get-caller-identity --query Account --output text)"

    - name: Build, tag, and push images to Amazon ECR
      env:
        ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
        ECR_REPOSITORY_PREFIX: roomiematcher
        AWS_ACCOUNT_ID: ${{ steps.get-aws-account.outputs.account_id }}
      run: |
        # Build and push each service
        for service in api-gateway-service auth-service profile-service match-service notification-service; do
          service_name=$(echo $service | sed 's/-service//')
          repo_name="${ECR_REPOSITORY_PREFIX}-${service_name}"
          
          # Create repository if it doesn't exist
          aws ecr describe-repositories --repository-names ${repo_name} || aws ecr create-repository --repository-name ${repo_name}
          
          # Build and push
          docker build -t ${ECR_REGISTRY}/${repo_name}:latest ./${service}
          docker push ${ECR_REGISTRY}/${repo_name}:latest
        done

    - name: Generate Dockerrun.aws.json
      env:
        AWS_ACCOUNT_ID: ${{ steps.get-aws-account.outputs.account_id }}
        AWS_REGION: ${{ secrets.AWS_REGION }}
        RDS_ENDPOINT: ${{ secrets.RDS_ENDPOINT }}
        DB_USERNAME: ${{ secrets.DB_USERNAME }}
        DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
        JWT_SECRET: ${{ secrets.JWT_SECRET }}
        AWS_SES_ACCESS_KEY: ${{ secrets.AWS_SES_ACCESS_KEY }}
        AWS_SES_SECRET_KEY: ${{ secrets.AWS_SES_SECRET_KEY }}
        AWS_SES_REGION: ${{ secrets.AWS_SES_REGION }}
        AWS_SES_FROM_EMAIL: ${{ secrets.AWS_SES_FROM_EMAIL }}
      run: |
        sed -e "s/\${AWS_ACCOUNT_ID}/$AWS_ACCOUNT_ID/g" \
            -e "s/\${AWS_REGION}/$AWS_REGION/g" \
            -e "s/\${RDS_ENDPOINT}/$RDS_ENDPOINT/g" \
            -e "s/\${DB_USERNAME}/$DB_USERNAME/g" \
            -e "s/\${DB_PASSWORD}/$DB_PASSWORD/g" \
            -e "s/\${JWT_SECRET}/$JWT_SECRET/g" \
            -e "s/\${AWS_SES_ACCESS_KEY}/$AWS_SES_ACCESS_KEY/g" \
            -e "s/\${AWS_SES_SECRET_KEY}/$AWS_SES_SECRET_KEY/g" \
            -e "s/\${AWS_SES_REGION}/$AWS_SES_REGION/g" \
            -e "s/\${AWS_SES_FROM_EMAIL}/$AWS_SES_FROM_EMAIL/g" \
            Dockerrun.aws.json > Dockerrun.aws.generated.json

    - name: Create deployment package
      run: |
        mkdir -p deploy_package
        cp -r .ebextensions deploy_package/
        cp Dockerrun.aws.generated.json deploy_package/Dockerrun.aws.json
        cd deploy_package && zip -r ../deploy.zip .

    - name: Deploy to Elastic Beanstalk
      uses: einaregilsson/beanstalk-deploy@v21
      with:
        aws_access_key: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws_secret_key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        application_name: roomiematcher
        environment_name: free-tier
        version_label: roomiematcher-${{ github.sha }}
        region: ${{ secrets.AWS_REGION }}
        deployment_package: deploy.zip
        wait_for_environment_recovery: 300
```

## Deployment Process

### Step 1: Initial Deployment

1. Ensure all GitHub Secrets are configured correctly
2. Push your code to the `main` branch:

```bash
git add .
git commit -m "Initial deployment"
git push origin main
```

3. The GitHub Actions workflow will automatically start
4. Monitor the progress in the "Actions" tab of your GitHub repository

### Step 2: Verify Deployment

1. Wait for the GitHub Actions workflow to complete (typically 10-15 minutes)
2. Go to the AWS Elastic Beanstalk console
3. Select your application (`roomiematcher`) and environment (`free-tier`)
4. Check the environment health and events
5. Once the environment is "Green/Healthy", click on the URL to access your application

## Monitoring and Troubleshooting

### Monitoring Your Application

1. **AWS Elastic Beanstalk Console**:
   - Health status
   - Recent events
   - Logs

2. **AWS CloudWatch**:
   - Metrics for CPU, memory, network usage
   - Alarm configuration

3. **Application Logs**:
   - Access via Elastic Beanstalk console or CloudWatch
   - Filter by log group/stream

4. **Free Tier Usage**:
   - Run the monitoring script:
   ```bash
   ./monitor-free-tier.sh
   ```

### Troubleshooting Common Issues

1. **Deployment Failures**:
   - Check GitHub Actions logs for build errors
   - Verify Elastic Beanstalk events for deployment issues
   - Ensure all environment variables are correctly set

2. **Application Errors**:
   - Check application logs in CloudWatch
   - SSH into the EC2 instance for direct troubleshooting:
   ```bash
   aws elasticbeanstalk retrieve-environment-info --environment-name free-tier --info-type tail
   ```

3. **Database Connection Issues**:
   - Verify RDS security group allows traffic from Elastic Beanstalk
   - Check database credentials in environment variables
   - Test connection using psql:
   ```bash
   psql -h <rds-endpoint> -U <username> -d roomie_auth
   ```

4. **Email Sending Issues**:
   - Verify SES is properly configured
   - Check if recipient emails are verified (required in sandbox mode)
   - Review SES sending limits and quotas

## Updating Your Application

### Making Code Changes

1. Make changes to your local repository
2. Test locally using Docker Compose:
```bash
docker-compose up
```
3. Commit and push changes:
```bash
git add .
git commit -m "Update feature X"
git push origin main
```
4. The GitHub Actions workflow will automatically deploy the updates

### Updating Environment Configuration

1. To update environment variables:
   - Update the corresponding GitHub Secret
   - Re-run the workflow from the "Actions" tab using the "workflow_dispatch" trigger

2. To update instance type or scaling options:
   - Modify the `env-config-minimal.json` file
   - Commit and push changes

## Cleanup and Resource Management

### Monitoring Costs

1. Set up AWS Budget alerts:
```bash
aws budgets create-budget --account-id $(aws sts get-caller-identity --query "Account" --output text) --budget '{"BudgetName":"FreeTier","BudgetLimit":{"Amount":"1","Unit":"USD"},"TimeUnit":"MONTHLY","BudgetType":"COST"}'
```

2. Regularly check the AWS Billing dashboard

### Cleaning Up Resources

When you no longer need the application, run:

```bash
./cleanup-free-tier.sh
```

This script will:
- Terminate the Elastic Beanstalk environment
- Delete the application
- Remove ECR repositories
- Delete the RDS instance
- Clean up other associated resources

## Conclusion

You have successfully set up a CI/CD pipeline to deploy RoomieMatcher to AWS Elastic Beanstalk using GitHub Actions. This deployment is optimized for AWS Free Tier usage, ensuring minimal costs while providing a fully functional application environment.

For additional customization options, refer to the AWS Elastic Beanstalk and GitHub Actions documentation. 
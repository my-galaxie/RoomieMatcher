# RoomieMatcher GitHub CI/CD Deployment Guide

This README provides a comprehensive guide for deploying the RoomieMatcher application to AWS Elastic Beanstalk using GitHub Actions CI/CD pipeline.

## Prerequisites

- GitHub account with this repository
- AWS account (Free Tier eligible)
- AWS CLI installed and configured

## Deployment Steps

### 1. Set Up AWS Resources

First, you need to set up the required AWS resources:

```bash
# Create the RDS database and other resources
./deploy-free-tier.sh
```

This script will:
- Create an RDS PostgreSQL instance
- Set up security groups
- Configure AWS SES for email notifications
- Initialize the databases
- Generate credentials

### 2. Configure GitHub Secrets

Go to your GitHub repository → Settings → Secrets and variables → Actions, and add these secrets:

| Secret Name | Description | Where to Find It |
|-------------|-------------|-----------------|
| `AWS_ACCESS_KEY_ID` | IAM user access key | From IAM user creation |
| `AWS_SECRET_ACCESS_KEY` | IAM user secret key | From IAM user creation |
| `AWS_REGION` | AWS region | Your chosen region (e.g., `ap-south-1`) |
| `RDS_ENDPOINT` | RDS instance endpoint | From `deploy-free-tier.sh` output |
| `DB_USERNAME` | Database username | From `deploy-free-tier.sh` output (usually `postgres`) |
| `DB_PASSWORD` | Database password | From `deploy-free-tier.sh` output |
| `JWT_SECRET` | Secret for JWT tokens | From `deploy-free-tier.sh` output |
| `AWS_SES_ACCESS_KEY` | SES access key | From `deploy-free-tier.sh` output |
| `AWS_SES_SECRET_KEY` | SES secret key | From `deploy-free-tier.sh` output |
| `AWS_SES_REGION` | SES region | Your chosen region (e.g., `ap-south-1`) |
| `AWS_SES_FROM_EMAIL` | Verified email for sending notifications | The email you verified in SES |

### 3. Deploy Using GitHub Actions

Once the secrets are configured, simply push your code to the `main` branch:

```bash
git add .
git commit -m "Initial deployment"
git push origin main
```

The GitHub Actions workflow will automatically:
1. Build the Java application
2. Create Docker images
3. Push images to Amazon ECR
4. Deploy to AWS Elastic Beanstalk

### 4. Monitor the Deployment

1. Go to the "Actions" tab in your GitHub repository to monitor the workflow
2. Once completed, access your application at the Elastic Beanstalk URL:
   `http://free-tier.[AWS_REGION].elasticbeanstalk.com`

## Manual Deployment

If you prefer to deploy manually or the CI/CD pipeline fails, you can run:

```bash
# Deploy to Elastic Beanstalk
./deploy-to-beanstalk.sh
```

## Updating Your Application

To update your application:

1. Make your code changes
2. Commit and push to the `main` branch
3. GitHub Actions will automatically deploy the updates

For manual deployment triggers, use the "Run workflow" button in the Actions tab.

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   - Check if security groups allow traffic from Elastic Beanstalk
   - Verify RDS endpoint and credentials in GitHub Secrets

2. **Docker Build Failures**
   - Check Dockerfile syntax
   - Ensure all required files are included in the build context

3. **Elastic Beanstalk Deployment Failures**
   - Check application logs in Elastic Beanstalk console
   - Verify environment variables are correctly set

### Viewing Logs

To view application logs:

1. Go to AWS Elastic Beanstalk console
2. Select your environment
3. Go to "Logs" → "Request Logs" → "Last 100 Lines"

## Cleanup

When you no longer need the application:

```bash
# Clean up all AWS resources
./cleanup-free-tier.sh
```

## Additional Resources

- [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elasticbeanstalk/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Multi-Container Configuration](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create_deploy_docker_v2config.html) 
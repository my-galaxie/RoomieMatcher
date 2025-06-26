# RoomieMatcher AWS Free Tier Deployment Guide

This guide provides instructions for deploying the RoomieMatcher application to AWS using free tier eligible resources.

## Overview

The RoomieMatcher application has been optimized for AWS free tier deployment with the following features:
- Single EC2 instance (t2.micro) without a load balancer
- Optimized memory usage for all services
- Reduced connection pool sizes
- Minimized logging to reduce CloudWatch costs
- Spot instances enabled for further cost reduction
- Proper AWS SES configuration with sandbox mode support

## Deployment Options

### Option 1: GitHub CI/CD Deployment (Recommended)

Follow the instructions in [GITHUB_DEPLOYMENT_README.md](GITHUB_DEPLOYMENT_README.md) to set up GitHub Actions for continuous deployment to AWS Elastic Beanstalk.

1. Configure GitHub repository secrets
2. Push changes to trigger deployment
3. Monitor deployment in GitHub Actions

### Option 2: Manual Deployment

Follow the instructions in [MANUAL_BEANSTALK_DEPLOYMENT.md](MANUAL_BEANSTALK_DEPLOYMENT.md) to manually deploy the application to AWS Elastic Beanstalk.

1. Build and package the application
2. Create Elastic Beanstalk application and environment
3. Upload and deploy the application

## Configuration Files

The following configuration files have been created or updated for AWS free tier deployment:

1. `.ebextensions/01_environment.config`: Basic environment settings
2. `.ebextensions/02_aws_resources.config`: AWS resources and permissions
3. `.ebextensions/03_database.config`: Database connection settings
4. `.ebextensions/04_container_commands.config`: Setup commands
5. `env-config-minimal.json`: Minimal configuration for free tier
6. `env-config-optimized.json`: Optimized configuration without load balancer

## Monitoring

Use the `monitor-free-tier.sh` script to monitor your AWS free tier usage:

```bash
./monitor-free-tier.sh
```

This script provides information about:
- EC2 instance usage
- RDS instance usage
- ECR repository usage
- Elastic Beanstalk environments
- CloudWatch metrics
- SES sending statistics
- CloudWatch Logs usage
- Cost Explorer data

## Optimization Details

For a comprehensive list of all optimizations made for AWS free tier, see [FREE_TIER_OPTIMIZATIONS.md](FREE_TIER_OPTIMIZATIONS.md).

For a summary of all project fixes, see [PROJECT_FIXES_SUMMARY.md](PROJECT_FIXES_SUMMARY.md).

## Important Notes

1. AWS Free Tier limits change over time. Always check the latest AWS Free Tier documentation.
2. The first 12 months of a new AWS account offer the most generous free tier benefits.
3. Some services may incur minimal charges even with these optimizations.
4. Set up AWS Budget alerts to be notified if costs approach free tier limits.
5. Consider running the application only when needed during development.

## AWS SES Configuration

The notification service is configured to use AWS SES for sending emails. In sandbox mode (default for new accounts), you must verify all sender and recipient email addresses.

To verify an email address:

```bash
aws ses verify-email-identity --email-address your-email@example.com --region us-east-1
```

## Troubleshooting

If you encounter issues during deployment, check the following:

1. **Database Connection**: Ensure RDS security group allows connections from the EC2 instance.
2. **SES Permissions**: Verify that the EC2 instance has proper permissions to use SES.
3. **Memory Issues**: Check if any service is running out of memory and adjust JVM settings.
4. **Logs**: Review CloudWatch logs for detailed error messages.

For additional help, refer to the AWS Elastic Beanstalk documentation or open an issue in the GitHub repository. 
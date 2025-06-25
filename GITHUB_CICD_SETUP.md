# GitHub CI/CD Pipeline Setup for RoomieMatcher

This guide will help you set up the GitHub CI/CD pipeline for deploying RoomieMatcher to AWS Elastic Beanstalk using the free tier resources.

## Prerequisites

- GitHub repository with RoomieMatcher code
- AWS account with Free Tier eligibility
- AWS RDS instance already created (or will be created by the deploy script)
- AWS SES configured for email sending

## Setting Up GitHub Secrets

The CI/CD workflow requires several GitHub secrets to be configured. Follow these steps:

1. Navigate to your GitHub repository
2. Go to **Settings** > **Secrets and variables** > **Actions**
3. Click on **New repository secret**
4. Add the following secrets:

| Secret Name | Description | Example Value |
|-------------|-------------|--------------|
| `AWS_ACCESS_KEY_ID` | AWS IAM user access key with deployment permissions | `AKIA1234567890ABCDEF` |
| `AWS_SECRET_ACCESS_KEY` | AWS IAM user secret key | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` |
| `AWS_REGION` | AWS region for deployment | `us-east-1` |
| `RDS_ENDPOINT` | RDS instance endpoint | `roomiematcher-db.cxyz123456.us-east-1.rds.amazonaws.com` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `YourStrongPassword` |
| `JWT_SECRET` | Secret key for JWT token generation | `YourJwtSecretKey` |
| `AWS_SES_ACCESS_KEY` | AWS SES access key | `AKIA1234567890GHIJKL` |
| `AWS_SES_SECRET_KEY` | AWS SES secret key | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY2` |
| `AWS_SES_FROM_EMAIL` | Verified email address for sending emails | `no-reply@yourdomain.com` |

## Required IAM Permissions

The AWS IAM user for CI/CD should have the following permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "elasticbeanstalk:*",
        "ec2:*",
        "ecs:*",
        "ecr:*",
        "elasticloadbalancing:*",
        "autoscaling:*",
        "cloudwatch:*",
        "s3:*",
        "sns:*",
        "cloudformation:*",
        "iam:PassRole"
      ],
      "Resource": "*"
    }
  ]
}
```

## How the CI/CD Pipeline Works

1. **Trigger**: The workflow is triggered when code is pushed to the `main` branch or manually using the workflow_dispatch event
2. **Build**: Java application is built using Maven
3. **Docker**: Docker images are built and pushed to Amazon ECR
4. **Configuration**: Dockerrun.aws.json is generated with the correct variables
5. **Deployment**: Application is deployed to AWS Elastic Beanstalk

## First Time Setup

For the first deployment, you should:

1. Run the `deploy-free-tier.sh` script locally to create the RDS instance and set up AWS SES
2. Copy the values from `roomiematcher-credentials.txt` file into GitHub Secrets
3. Push code to GitHub to trigger the automated deployment

## Monitoring Deployments

You can monitor the deployment status:

1. In GitHub: Go to **Actions** tab in your repository
2. In AWS: Check the Elastic Beanstalk console

## Troubleshooting

If deployment fails:

1. Check the GitHub Actions logs for error messages
2. Verify that all secrets are properly configured
3. Ensure your AWS IAM user has sufficient permissions
4. Check the Elastic Beanstalk logs for application-specific errors

## Staying Within Free Tier

This CI/CD pipeline is configured to deploy to AWS Free Tier resources, but be cautious:

1. Keep only one environment running
2. Use `db.t2.micro` for RDS (free tier eligible)
3. Monitor your AWS billing regularly
4. Set up AWS Budget alerts 
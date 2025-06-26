# RoomieMatcher: Quick GitHub CI/CD Setup Guide

This guide provides simplified steps to deploy RoomieMatcher to AWS Elastic Beanstalk using GitHub Actions.

## Step 1: Set Up AWS Resources

1. **Create an IAM User for Deployments**:
   - Go to AWS IAM Console
   - Create a new user with programmatic access
   - Attach policies: `AmazonEC2ContainerRegistryFullAccess`, `AWSElasticBeanstalkFullAccess`, and `AmazonS3FullAccess`
   - Save the Access Key ID and Secret Access Key

2. **Set Up RDS Database**:
   - Run `./deploy-free-tier.sh` to create the RDS instance
   - Note the RDS endpoint

3. **Configure AWS SES**:
   - Verify an email address in SES console
   - Create SES credentials if needed

## Step 2: Configure GitHub Secrets

1. Go to your GitHub repository → Settings → Secrets and variables → Actions
2. Add the following secrets:

| Secret Name | Description |
|-------------|-------------|
| `AWS_ACCESS_KEY_ID` | IAM user access key |
| `AWS_SECRET_ACCESS_KEY` | IAM user secret key |
| `AWS_REGION` | AWS region (e.g., `ap-south-1`) |
| `RDS_ENDPOINT` | RDS instance endpoint |
| `DB_USERNAME` | Database username (usually `postgres`) |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret for JWT tokens |
| `AWS_SES_ACCESS_KEY` | SES access key |
| `AWS_SES_SECRET_KEY` | SES secret key |
| `AWS_SES_REGION` | SES region |
| `AWS_SES_FROM_EMAIL` | Verified email for sending notifications |

## Step 3: Push Code to GitHub

The GitHub Actions workflow is already set up in `.github/workflows/deploy-to-aws.yml`. Simply push your code to the `main` branch:

```bash
git add .
git commit -m "Initial deployment"
git push origin main
```

## Step 4: Monitor Deployment

1. Go to the "Actions" tab in your GitHub repository
2. Watch the workflow progress
3. Once completed, check your application at the Elastic Beanstalk URL

## Troubleshooting

If deployment fails, check:

1. **GitHub Actions Logs**:
   - Look for error messages in the workflow run

2. **AWS Elastic Beanstalk Console**:
   - Check environment health
   - Review events for errors

3. **Common Issues**:
   - Incorrect AWS credentials
   - Missing GitHub secrets
   - Docker build failures
   - RDS connectivity issues

## Updating Your Application

To update your application, simply push changes to the `main` branch. The CI/CD pipeline will automatically build and deploy the updates.

For manual deployment triggers, use the "Run workflow" button in the Actions tab.

## Cleanup

When you no longer need the application:

1. Run `./cleanup-free-tier.sh` to remove AWS resources
2. Delete the GitHub repository if needed 
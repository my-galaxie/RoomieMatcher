# RoomieMatcher Microservices - AWS Deployment Checklist

Use this checklist to ensure you have completed all necessary steps for deploying the RoomieMatcher microservices to AWS.

## Pre-Deployment Checklist

### AWS Account and Access
- [ ] AWS account with administrative access
- [ ] AWS CLI installed and configured with appropriate credentials
- [ ] AWS region selected based on target audience location

### GitHub Repository
- [ ] Code pushed to GitHub repository
- [ ] GitHub OAuth token with repo access created
- [ ] All microservices code is complete and tested locally

### AWS Services Configuration
- [ ] AWS SES configured and verified domain/email
- [ ] Moved out of SES sandbox for production deployments
- [ ] Decided on database instance sizes based on expected load
- [ ] Planned VPC CIDR ranges for all environments

### Security
- [ ] Generated strong JWT secret
- [ ] Created secure database password
- [ ] Secured AWS SES credentials
- [ ] Reviewed IAM roles and permissions

## Deployment Checklist

### Environment Configuration
- [ ] Created environment-specific configuration file
- [ ] Set appropriate environment name (dev, staging, prod)
- [ ] Configured all required parameters

### CloudFormation Templates
- [ ] Reviewed VPC template and parameters
- [ ] Reviewed RDS template and parameters
- [ ] Reviewed ECS cluster template and parameters
- [ ] Reviewed service discovery template and parameters
- [ ] Reviewed task definitions template and parameters
- [ ] Reviewed load balancer template and parameters
- [ ] Reviewed CI/CD pipeline template and parameters

### Deployment Execution
- [ ] Run deployment script with appropriate parameters
- [ ] Monitor CloudFormation stack creation progress
- [ ] Verify all resources created successfully
- [ ] Check CodePipeline execution status

## Post-Deployment Checklist

### Verification
- [ ] Verify load balancer is accessible
- [ ] Test API Gateway endpoints
- [ ] Verify service-to-service communication
- [ ] Test authentication flow
- [ ] Test profile creation and matching functionality
- [ ] Verify email notifications

### Monitoring and Logging
- [ ] Check CloudWatch logs for each service
- [ ] Set up CloudWatch alarms for critical metrics
- [ ] Configure log retention policies
- [ ] Set up monitoring dashboard

### Security Review
- [ ] Verify security groups are properly configured
- [ ] Ensure databases are not publicly accessible
- [ ] Check that secrets are properly managed
- [ ] Verify HTTPS is configured for production

### Documentation
- [ ] Document deployed architecture
- [ ] Record all endpoints and access URLs
- [ ] Document deployment process for team members
- [ ] Update README with deployment instructions

## Maintenance Checklist

### Backup and Recovery
- [ ] Configure database backups
- [ ] Test database restoration procedure
- [ ] Document disaster recovery process

### Updates and Patches
- [ ] Plan regular update schedule
- [ ] Document update procedure
- [ ] Test update process in development environment

### Scaling
- [ ] Monitor resource utilization
- [ ] Adjust auto-scaling parameters as needed
- [ ] Plan for database scaling if necessary

### Cost Management
- [ ] Set up AWS cost alerts
- [ ] Review resource utilization regularly
- [ ] Optimize instance types and sizes 
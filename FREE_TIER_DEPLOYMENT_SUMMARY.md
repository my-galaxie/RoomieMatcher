# RoomieMatcher AWS Free Tier Deployment - Optimization Summary

This document summarizes the changes made to optimize the RoomieMatcher application for AWS Free Tier deployment.

## Key Optimizations

### 1. Database Configuration
- Configured RDS to use a single db.t2.micro instance (Free Tier eligible)
- Disabled Multi-AZ deployment to stay within Free Tier limits
- Set up multiple databases on a single RDS instance for all services
- Updated database initialization scripts to include new tenant preference schema

### 2. Memory Optimization
- Reduced container memory allocations from 150MB to 128MB per service
- Optimized JVM settings with lower heap sizes (96MB max, 48MB min)
- Added G1GC garbage collector settings for better memory management
- Disabled Hibernate statistics generation to reduce memory usage
- Enabled JDBC batch processing to improve database efficiency

### 3. AWS SES Integration
- Added support for AWS SES sandbox mode
- Implemented email verification endpoints
- Added proper error handling for unverified email addresses
- Updated EmailRequestDTO to support multiple recipients
- Added fallback to instance profile credentials when no explicit SES credentials are provided

### 4. Elastic Beanstalk Configuration
- Configured for Single Instance environment (no load balancer)
- Added JVM options through .ebextensions
- Set up proper IAM roles for SES access
- Removed local PostgreSQL container to use RDS instead

### 5. Monitoring and Management
- Enhanced monitoring script to check Free Tier resource usage
- Added detailed error handling and region specification
- Added sandbox mode detection for SES
- Improved cleanup script for removing all AWS resources

### 6. Bug Fixes
- Fixed EmailRequestDTO to use List<String> for recipients instead of a single String
- Updated StandardEmailService to handle multiple recipients
- Added proper error handling in AWS SES service
- Fixed credential handling in AWS services to support instance profiles

## Deployment Files

The following files were updated or created:

1. **Dockerrun.aws.json** - Optimized for Free Tier with reduced memory and removed local PostgreSQL
2. **.ebextensions/01_jvm_options.config** - Added JVM optimization settings
3. **.ebextensions/02_aws_resources.config** - Added IAM permissions for SES
4. **deploy-free-tier.sh** - Script for deploying to AWS Free Tier
5. **cleanup-free-tier.sh** - Script for removing all AWS resources
6. **monitor-free-tier.sh** - Enhanced script for monitoring Free Tier usage
7. **init-db/setup-rds-databases.sh** - Updated to include tenant preferences schema

## Service Updates

### Notification Service
- Enhanced AWS SES integration with proper error handling
- Added support for sandbox mode and email verification
- Updated controller with verification endpoints
- Fixed email recipient handling

### All Services
- Optimized memory usage and JVM settings
- Added database connection pooling optimizations
- Configured for external RDS connection

## Deployment Instructions

To deploy the application to AWS Free Tier:

1. Run the `deploy-free-tier.sh` script
2. Monitor usage with `monitor-free-tier.sh`
3. When finished, clean up with `cleanup-free-tier.sh`

For detailed deployment steps, refer to the `FREE_TIER_DEPLOYMENT.md` document. 
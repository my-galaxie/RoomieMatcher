# Setting Up a Shared RDS PostgreSQL Instance for Microservices

This guide explains how to set up a single AWS RDS PostgreSQL instance with multiple databases for the RoomieMatcher microservices application.

## Benefits of Using a Single RDS Instance

- **Cost Efficiency**: Significantly reduces costs by using one instance instead of multiple instances
- **Simplified Management**: Easier to manage, backup, and monitor a single instance
- **Logical Isolation**: Each service still has its own database for isolation
- **Consistent Performance**: All services benefit from the same instance specifications

## Prerequisites

- AWS Account with appropriate permissions
- AWS CLI installed and configured
- PostgreSQL client (psql) installed locally

## Step 1: Create the RDS Instance using CloudFormation

1. Navigate to the `deployment/cloudformation/database` directory
2. Deploy the RDS CloudFormation template:

```bash
aws cloudformation create-stack \
  --stack-name roomiematcher-db \
  --template-body file://rds.yaml \
  --parameters \
    ParameterKey=VpcId,ParameterValue=<your-vpc-id> \
    ParameterKey=SubnetIds,ParameterValue="<subnet-id-1>,<subnet-id-2>" \
    ParameterKey=DBUsername,ParameterValue=<master-username> \
    ParameterKey=DBPassword,ParameterValue=<master-password> \
    ParameterKey=Environment,ParameterValue=prod
```

3. Wait for the stack creation to complete:

```bash
aws cloudformation wait stack-create-complete --stack-name roomiematcher-db
```

4. Get the RDS endpoint:

```bash
aws cloudformation describe-stacks \
  --stack-name roomiematcher-db \
  --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" \
  --output text
```

## Step 2: Initialize the Databases

1. Run the database initialization script:

```bash
cd deployment/scripts
./setup-rds-databases.sh <rds-endpoint> <master-username> <master-password>
```

This script will:
- Connect to the RDS instance
- Create separate databases for each service (auth_db, profile_db, match_db, notification_db)
- Create dedicated users for each service
- Grant appropriate permissions
- Initialize the schema for each database

## Step 3: Set Up Environment Variables

1. Generate environment variables for your services:

```bash
./setup-env-variables.sh <rds-endpoint> env-variables.txt
```

2. Keep the generated credentials secure and use them to configure your deployment environment.

## Step 4: Update Application Configurations

The application is already configured to use environment variables for database connections:

- `RDS_ENDPOINT`: The endpoint of your RDS instance
- `AUTH_DB_USERNAME`, `AUTH_DB_PASSWORD`: Auth service database credentials
- `PROFILE_DB_USERNAME`, `PROFILE_DB_PASSWORD`: Profile service database credentials
- `MATCH_DB_USERNAME`, `MATCH_DB_PASSWORD`: Match service database credentials
- `NOTIFICATION_DB_USERNAME`, `NOTIFICATION_DB_PASSWORD`: Notification service database credentials

## Step 5: Deploy Your Application

1. For AWS Elastic Beanstalk deployment:
   - Update your environment variables in the Elastic Beanstalk environment
   - Deploy your application using the existing deployment scripts

2. For ECS/Kubernetes deployment:
   - Update your environment variables in your container configuration
   - Deploy your containers

## Monitoring and Maintenance

- **Monitoring**: Set up CloudWatch alarms for the RDS instance to monitor CPU, memory, storage, and connections
- **Backups**: Configure automated backups for your RDS instance
- **Scaling**: Monitor the performance and scale the instance type as needed

## Troubleshooting

- **Connection Issues**: Ensure your security groups allow traffic on port 5432 from your application servers
- **Performance Issues**: Monitor slow queries and optimize as needed
- **Space Issues**: Monitor storage usage and increase allocated storage if needed

## Local Development

For local development, the `docker-compose.yml` file is configured to use a single PostgreSQL container with multiple databases, mirroring the production setup.

To start the local development environment:

```bash
docker-compose up -d
```

This will create a PostgreSQL container with all required databases and start all microservices. 
# RoomieMatcher AWS Cost Optimization Guide

This guide provides strategies for optimizing costs when deploying the RoomieMatcher application on AWS Elastic Beanstalk.

## Cost Breakdown

The standard deployment of RoomieMatcher on AWS Elastic Beanstalk has the following estimated monthly costs:

| Resource | Specification | Monthly Cost (USD) |
|----------|---------------|-------------------|
| EC2 Instance | t2.small (2 GB RAM, 1 vCPU) | $16.80 |
| Load Balancer | Application Load Balancer | $16.20 |
| RDS Instance | db.t3.micro (1 GB RAM) | $12.24 |
| S3 Storage | 5 GB | $0.12 |
| ECR Storage | 1 GB | $0.10 |
| Data Transfer | 50 GB | $4.50 |
| **Total** | | **$49.96** |

## Cost Optimization Strategies

### 1. Leverage AWS Free Tier

For new AWS accounts (less than 12 months old), you can utilize the AWS Free Tier:

| Service | Free Tier Offering | Savings |
|---------|-------------------|---------|
| EC2 | 750 hours of t2.micro per month | $8.40 |
| RDS | 750 hours of db.t2.micro per month | $12.24 |
| S3 | 5 GB of storage | $0.12 |
| Load Balancer | Not included in free tier | $0 |

**Potential Monthly Cost with Free Tier**: $20.30 (59% savings)

### 2. Minimal Production Deployment

For a minimal production deployment with reduced resources:

1. **Use Single t2.micro Instance**:
   - Replace t2.small with t2.micro
   - Savings: $8.40/month

2. **Use Shared Database with Multiple Databases**:
   - Already implemented: Single RDS instance with multiple databases
   - Savings: Already optimized

3. **Optimize Load Balancer Usage**:
   - Use a Classic Load Balancer instead of Application Load Balancer
   - Savings: $8.10/month

**Potential Monthly Cost with Minimal Deployment**: $33.46 (33% savings)

### 3. Free Tier Development Environment

For development and testing purposes, you can deploy a completely free setup:

1. **Use Free Tier EC2**:
   - t2.micro instance (750 hours/month free)

2. **Eliminate Load Balancer**:
   - Access the application directly via EC2 instance
   - Savings: $16.20/month

3. **Use Local Database**:
   - Run PostgreSQL in a container on the EC2 instance
   - Savings: $12.24/month

4. **Minimize S3 and ECR Usage**:
   - Stay within free tier limits
   - Savings: $0.22/month

**Potential Monthly Cost with Free Tier Development**: $0.00 for first 12 months

### 4. Serverless Alternative

For a more scalable and potentially cost-effective solution:

1. **Use AWS Fargate**:
   - Pay only for the resources you use
   - Estimated cost: $20-30/month depending on traffic

2. **Use Aurora Serverless**:
   - Pay only for database usage
   - Estimated cost: $10-15/month for low traffic

3. **Use API Gateway**:
   - Replace the API Gateway service with AWS API Gateway
   - Estimated cost: $5-10/month for moderate traffic

**Potential Monthly Cost with Serverless**: $35-55/month (variable based on usage)

## Implementation Instructions

### Option 1: Free Tier Development (1 Week Deployment)

To implement a completely free deployment for development and testing:

1. **Update Instance Type in `.ebextensions/01_environment.config`**:

```yaml
aws:autoscaling:launchconfiguration:
  InstanceType: t2.micro
```

2. **Remove Load Balancer Configuration in `.ebextensions/01_environment.config`**:

```yaml
aws:elasticbeanstalk:environment:
  LoadBalancerType: none
```

3. **Update Dockerrun.aws.json to Use Local PostgreSQL**:
   - Keep the PostgreSQL container configuration
   - Remove separate database volume if not needed

4. **Deploy with Limited Resources**:

```bash
aws elasticbeanstalk create-environment \
    --application-name roomiematcher \
    --environment-name roomiematcher-dev \
    --solution-stack-name "64bit Amazon Linux 2 v3.5.3 running Docker" \
    --tier "WebServer" \
    --version-label v1 \
    --option-settings file://env-config-minimal.json \
    --region <your-region>
```

### Option 2: Minimal Production Deployment

For a cost-effective production deployment:

1. **Update Instance Type in `.ebextensions/01_environment.config`**:

```yaml
aws:autoscaling:launchconfiguration:
  InstanceType: t2.micro
```

2. **Use Classic Load Balancer in `.ebextensions/01_environment.config`**:

```yaml
aws:elasticbeanstalk:environment:
  LoadBalancerType: classic
```

3. **Configure Auto Scaling for Cost Efficiency**:

```yaml
aws:autoscaling:asg:
  MinSize: 1
  MaxSize: 2
```

4. **Optimize RDS Instance**:
   - Already implemented: Using a single RDS instance with multiple databases
   - Consider using a burstable instance type like db.t4g.micro for better price/performance

5. **Deploy with Optimized Settings**:

```bash
aws elasticbeanstalk create-environment \
    --application-name roomiematcher \
    --environment-name roomiematcher-prod \
    --solution-stack-name "64bit Amazon Linux 2 v3.5.3 running Docker" \
    --tier "WebServer" \
    --version-label v1 \
    --option-settings file://env-config-optimized.json \
    --region <your-region>
```

### Option 3: Scheduled Scaling

To further reduce costs, implement scheduled scaling:

1. **Create a file `.ebextensions/08_scheduled_actions.config`**:

```yaml
Resources:
  ScaleDownScheduledAction:
    Type: AWS::AutoScaling::ScheduledAction
    Properties:
      AutoScalingGroupName: { "Ref" : "AWSEBAutoScalingGroup" }
      DesiredCapacity: 0
      MinSize: 0
      MaxSize: 0
      Recurrence: "0 0 * * *"  # Midnight UTC

  ScaleUpScheduledAction:
    Type: AWS::AutoScaling::ScheduledAction
    Properties:
      AutoScalingGroupName: { "Ref" : "AWSEBAutoScalingGroup" }
      DesiredCapacity: 1
      MinSize: 1
      MaxSize: 2
      Recurrence: "0 8 * * *"  # 8 AM UTC
```

This configuration will scale down the environment during non-business hours to save costs.

### Option 4: RDS Optimization

Our current setup already uses a single RDS instance with multiple databases, which is cost-efficient. Additional optimizations include:

1. **Use Graviton-based Instances**:
   - Replace db.t3.micro with db.t4g.micro for better price/performance
   - Potential savings: ~20% on RDS costs

2. **Configure RDS Storage Auto-scaling**:
   - Set maximum storage threshold to avoid over-provisioning
   - Only pay for what you need

```yaml
Resources:
  AWSEBRDSDatabase:
    Type: AWS::RDS::DBInstance
    Properties:
      MaxAllocatedStorage: 50  # Maximum storage in GB
```

3. **Schedule RDS Instance Stopping**:
   - For development environments, stop RDS instances during non-business hours
   - Use AWS Lambda and EventBridge for scheduling

## Comparison of Deployment Options

| Deployment Option | Monthly Cost | Pros | Cons |
|-------------------|--------------|------|------|
| Standard Deployment | $49.96 | Full production setup | Higher cost |
| Minimal Production | $33.46 | Production-ready, lower cost | Limited resources |
| Free Tier Development | $0.00 (first 12 months) | No cost for development | Limited resources, not for production |
| Serverless Alternative | $35-55 | Scalable, pay per use | More complex setup |

## Monitoring and Optimization

1. **Set Up AWS Cost Explorer**:
   - Enable AWS Cost Explorer to track your spending
   - Create custom reports to identify cost drivers

2. **Configure Budget Alerts**:
   - Set up AWS Budgets to get notifications when costs exceed thresholds

```bash
aws budgets create-budget \
    --account-id <your-account-id> \
    --budget file://budget.json \
    --notifications-with-subscribers file://notifications.json
```

3. **Regularly Review Resource Utilization**:
   - Use CloudWatch to monitor resource utilization
   - Rightsize instances based on actual usage

```bash
# Monitor EC2 instances
aws cloudwatch get-metric-statistics \
    --namespace AWS/EC2 \
    --metric-name CPUUtilization \
    --dimensions Name=InstanceId,Value=<instance-id> \
    --start-time 2023-01-01T00:00:00Z \
    --end-time 2023-01-07T00:00:00Z \
    --period 86400 \
    --statistics Average

# Monitor RDS instance
aws cloudwatch get-metric-statistics \
    --namespace AWS/RDS \
    --metric-name CPUUtilization \
    --dimensions Name=DBInstanceIdentifier,Value=<db-instance-id> \
    --start-time 2023-01-01T00:00:00Z \
    --end-time 2023-01-07T00:00:00Z \
    --period 86400 \
    --statistics Average
```

4. **Database Performance Monitoring**:
   - Enable Enhanced Monitoring for RDS
   - Review slow query logs to optimize database performance

```bash
aws rds modify-db-instance \
    --db-instance-identifier <db-instance-id> \
    --monitoring-interval 60 \
    --monitoring-role-arn <monitoring-role-arn> \
    --region <your-region>
```

## Conclusion

By implementing these cost optimization strategies, you can reduce the cost of running the RoomieMatcher application on AWS Elastic Beanstalk by 33-100%, depending on your requirements and eligibility for the AWS Free Tier.

The current architecture already implements a significant cost optimization by using a single RDS instance with multiple databases, providing logical isolation without incurring separate costs per database instance.

For development and testing purposes, you can deploy the application at no cost for the first 12 months. For production deployments, you can optimize costs to around $33.46 per month while maintaining reliability and performance. 
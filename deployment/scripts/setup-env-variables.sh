#!/bin/bash

# Script to generate environment variable configuration for connecting to a shared RDS instance
# Usage: ./setup-env-variables.sh <rds_endpoint> <output_file>

# Check if required parameters are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <rds_endpoint> <output_file>"
    exit 1
fi

RDS_ENDPOINT=$1
OUTPUT_FILE=$2

# Generate secure random passwords for each service
AUTH_PASSWORD=$(openssl rand -base64 16)
PROFILE_PASSWORD=$(openssl rand -base64 16)
MATCH_PASSWORD=$(openssl rand -base64 16)
NOTIFICATION_PASSWORD=$(openssl rand -base64 16)

# Create environment variable file
cat > $OUTPUT_FILE << EOF
# RDS Configuration
RDS_ENDPOINT=$RDS_ENDPOINT

# Auth Service Database
AUTH_DB_USERNAME=auth_user
AUTH_DB_PASSWORD=$AUTH_PASSWORD

# Profile Service Database
PROFILE_DB_USERNAME=profile_user
PROFILE_DB_PASSWORD=$PROFILE_PASSWORD

# Match Service Database
MATCH_DB_USERNAME=match_user
MATCH_DB_PASSWORD=$MATCH_PASSWORD

# Notification Service Database
NOTIFICATION_DB_USERNAME=notification_user
NOTIFICATION_DB_PASSWORD=$NOTIFICATION_PASSWORD

# JWT Configuration
JWT_SECRET=$(openssl rand -base64 32)

# Other common environment variables can be added here
EOF

echo "Environment variables have been written to $OUTPUT_FILE"
echo "IMPORTANT: Keep this file secure and do not commit it to version control!"
echo "Use these variables to update your AWS Elastic Beanstalk environment or container environment variables."

exit 0 
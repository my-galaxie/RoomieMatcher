#!/bin/bash

# Script to initialize multiple databases in a single RDS PostgreSQL instance
# Usage: ./setup-rds-databases.sh <rds_endpoint> <master_username> <master_password>

# Check if required parameters are provided
if [ "$#" -ne 3 ]; then
    echo "Usage: $0 <rds_endpoint> <master_username> <master_password>"
    exit 1
fi

RDS_ENDPOINT=$1
MASTER_USERNAME=$2
MASTER_PASSWORD=$3

echo "Connecting to RDS instance at $RDS_ENDPOINT and initializing databases..."

# Use PGPASSWORD environment variable to avoid password prompt
export PGPASSWORD=$MASTER_PASSWORD

# Execute the initialization SQL script
psql -h $RDS_ENDPOINT -U $MASTER_USERNAME -d postgres -f ../init-db/rds-init.sql

# Check if the command was successful
if [ $? -eq 0 ]; then
    echo "Database initialization completed successfully!"
else
    echo "Error: Database initialization failed!"
    exit 1
fi

echo "All databases and users have been created successfully."
echo "Remember to update your application environment variables with the appropriate database connection details."

# Unset password environment variable for security
unset PGPASSWORD

exit 0 
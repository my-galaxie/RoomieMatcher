#!/bin/bash

set -e

# Check for required arguments
if [ $# -lt 3 ]; then
    echo "Usage: $0 <rds-endpoint> <master-username> <master-password>"
    exit 1
fi

RDS_ENDPOINT=$1
DB_USERNAME=$2
DB_PASSWORD=$3
DB_PORT=5432

echo "Setting up databases on RDS instance at $RDS_ENDPOINT..."

# List of databases to create
DATABASES=("roomie_auth" "roomie_profile" "roomie_match" "roomie_notification")

# PostgreSQL connection string
PGPASSWORD=$DB_PASSWORD

# Check connection to PostgreSQL
echo "Testing connection to PostgreSQL..."
if ! psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -c "SELECT version();" postgres; then
    echo "Failed to connect to PostgreSQL. Please check your credentials and endpoint."
    exit 1
fi

# Create databases
for DB_NAME in "${DATABASES[@]}"; do
    echo "Creating database $DB_NAME..."
    
    # Check if database exists
    DB_EXISTS=$(psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" postgres)
    
    if [ "$DB_EXISTS" = "1" ]; then
        echo "Database $DB_NAME already exists."
    else
        psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -c "CREATE DATABASE $DB_NAME;" postgres
        echo "Database $DB_NAME created successfully."
    fi
done

# Initialize database schemas
echo "Initializing database schemas..."

# Auth service schema
echo "Initializing auth service schema..."
psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -d roomie_auth -f ../auth-service/src/main/resources/db/migration/V1__init_schema.sql

# Profile service schema
echo "Initializing profile service schema..."
psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -d roomie_profile -f ../profile-service/src/main/resources/db/migration/V1__init_schema.sql
psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -d roomie_profile -f ../profile-service/src/main/resources/db/migration/V2__add_tenant_preferences.sql

# Match service schema
echo "Initializing match service schema..."
psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -d roomie_match -f ../match-service/src/main/resources/db/migration/V1__init_schema.sql

# Create a simple schema for notification service if it doesn't have one
echo "Initializing notification service schema..."
psql -h $RDS_ENDPOINT -p $DB_PORT -U $DB_USERNAME -d roomie_notification -c "
CREATE TABLE IF NOT EXISTS email_notifications (
    id SERIAL PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING'
);"

echo "Database initialization complete!" 
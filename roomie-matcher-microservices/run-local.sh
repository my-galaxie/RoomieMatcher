#!/bin/bash

# Stop execution if any command fails
set -e

echo "RoomieMatcher Microservices - Local Development"
echo "==============================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven and try again."
    exit 1
fi

# Check if services should be built
BUILD=false
if [ "$1" == "--build" ]; then
    BUILD=true
fi

# Build modules if --build flag is provided
if [ "$BUILD" = true ]; then
    echo "[1/6] Building all modules..."
    mvn clean install -DskipTests
    echo "Build successful."
else
    echo "Skipping build. Use --build to build the modules."
fi

# Check if a PostgreSQL instance is running
echo "[2/6] Checking PostgreSQL..."
if ! pg_isready -h localhost -p 5432 &> /dev/null; then
    echo "PostgreSQL is not running on localhost:5432. Starting Docker container..."
    
    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        echo "Docker is not installed. Please install Docker and try again."
        exit 1
    fi
    
    echo "Starting PostgreSQL container..."
    docker run --name roomie-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -d -p 5432:5432 postgres:15-alpine
    
    echo "Waiting for PostgreSQL to start..."
    sleep 10
fi

# Create databases if they don't exist
echo "[3/6] Creating databases if needed..."
PGPASSWORD=postgres psql -h localhost -U postgres -c "CREATE DATABASE IF NOT EXISTS roomie_auth;"
PGPASSWORD=postgres psql -h localhost -U postgres -c "CREATE DATABASE IF NOT EXISTS roomie_profile;"
PGPASSWORD=postgres psql -h localhost -U postgres -c "CREATE DATABASE IF NOT EXISTS roomie_match;"

# Start services in the background
echo "[4/6] Starting all services..."

# Start auth-service
echo "Starting auth-service..."
cd auth-service
mvn spring-boot:run > ../auth-service.log 2>&1 &
AUTH_PID=$!
cd ..

# Wait for auth-service to start
echo "Waiting for auth-service to start..."
sleep 20

# Start profile-service
echo "Starting profile-service..."
cd profile-service
mvn spring-boot:run > ../profile-service.log 2>&1 &
PROFILE_PID=$!
cd ..

# Wait for profile-service to start
echo "Waiting for profile-service to start..."
sleep 20

# Start match-service
echo "Starting match-service..."
cd match-service
mvn spring-boot:run > ../match-service.log 2>&1 &
MATCH_PID=$!
cd ..

# Wait for match-service to start
echo "Waiting for match-service to start..."
sleep 20

# Start notification-service
echo "Starting notification-service..."
cd notification-service
mvn spring-boot:run > ../notification-service.log 2>&1 &
NOTIFICATION_PID=$!
cd ..

echo "[5/6] All services started. Press Ctrl+C to stop all services."

# Wait for user to press Ctrl+C
echo "[6/6] Service endpoints:"
echo "  Auth Service:        http://localhost:8081/api/v1"
echo "  Profile Service:     http://localhost:8082/api/v1"
echo "  Match Service:       http://localhost:8083/api/v1"
echo "  Notification Service: http://localhost:8084/api/v1"

# Trap Ctrl+C and stop all services
trap 'kill $AUTH_PID $PROFILE_PID $MATCH_PID $NOTIFICATION_PID; echo "Stopping all services..."; exit 0' INT

# Wait for all services to finish
wait

#!/bin/bash

# Script to delete the duplicate roomie-matcher-microservices directory
# after all necessary files have been merged into the root directory.

# Check for -y flag for non-interactive mode
if [ "$1" == "-y" ]; then
    CONFIRM="y"
else
    echo "This script will delete the roomie-matcher-microservices directory."
    echo "Make sure you have already merged all necessary files into the root directory."
    echo ""
    read -p "Are you sure you want to continue? (y/n): " CONFIRM
fi

if [ "$CONFIRM" != "y" ]; then
    echo "Operation cancelled."
    exit 0
fi

echo "Deleting roomie-matcher-microservices directory..."
rm -rf roomie-matcher-microservices

if [ $? -eq 0 ]; then
    echo "Successfully deleted roomie-matcher-microservices directory."
else
    echo "Failed to delete roomie-matcher-microservices directory."
    exit 1
fi

echo ""
echo "Cleanup complete. The project now has a single directory structure."
echo "Make sure to run 'mvn clean compile' to verify everything works correctly." 
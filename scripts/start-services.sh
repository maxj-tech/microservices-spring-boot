#!/bin/bash

# Ensure the script is executed from the project root directory
cd "$(dirname "$0")/.."

# Source service paths
config_path="scripts/service-paths-config.sh"
if [ -f "$config_path" ]; then
    source "$config_path"
else
    echo "Configuration file not found at $config_path"
    exit 1
fi

# Perform clean or build if requested
if [ "$1" == "clean" ]; then
    ./gradlew clean build
elif [ "$1" == "build" ]; then
    ./gradlew build
fi

# Start the services and capture the PIDs
java -jar $product_comp_service &
java -jar $product_service &
java -jar $recommendation_service &
java -jar $review_service &

echo "All services started successfully."

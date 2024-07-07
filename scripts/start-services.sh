#!/bin/bash
set -e

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

# Check for the "docker" flag
docker=false
if [[ $@ == *"docker"* ]]; then
  docker=true
fi

# Perform clean or build if requested
if [[ $@ == *"clean"* ]]; then
  ./gradlew clean build
elif [[ $@ == *"build"* ]]; then
  ./gradlew build
fi

# Start the services based on the flag
if [ $docker == true ]; then
  docker compose up -d
else
  java -jar $product_comp_service & comp_pid=$!
  java -jar $product_service & prod_pid=$!
  java -jar $recommendation_service & rec_pid=$!
  java -jar $review_service & rev_pid=$!

  wait $comp_pid $prod_pid $rec_pid $rev_pid
fi

echo "All services started successfully."

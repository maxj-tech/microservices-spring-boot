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

# Function to remove the wildcard from the path
remove_wildcard() {
    echo "${1%*.jar}" # This removes "*.jar" from the end of the string
}

# Stop the services based on the JAR path without wildcards
echo "Stopping services..."
pkill -f "$(remove_wildcard "$product_comp_service")"
pkill -f "$(remove_wildcard "$product_service")"
pkill -f "$(remove_wildcard "$recommendation_service")"
pkill -f "$(remove_wildcard "$review_service")"

echo "All services stopped."

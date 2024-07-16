#!/bin/bash
# scripts/stop-services.sh
# Stops Docker services defined in a project using docker-compose and pkill.
#
# Usage: ./stop-services.sh
# Dependencies: Docker, docker-compose, 'service-paths-config.sh' defining service paths.
#
# Notes:
# - Executes from the project root directory.
# - Uses 'service-paths-config.sh' for service paths.
# - Stops services with docker-compose down --remove-orphans.
# - Terminates remaining service processes with pkill based on JAR paths.
# - Ensure correct 'service-paths-config.sh' configuration to avoid unintended termination.
#
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

# Function to remove the wildcard from the path
remove_wildcard() {
  echo "${1%*.jar}" # This removes "*.jar" from the end of the string
}

# Stop the services based on the JAR path without wildcards
echo "Stopping services..."

# Run docker-compose down and check exit code
docker compose down --remove-orphans
docker_compose_down_exit_code=$?

if [ $docker_compose_down_exit_code -eq 0 ]; then
  # Use pkill only if docker-compose down might have left behind processes
  pkill -f "$(remove_wildcard "$product_comp_service")" || true
  pkill -f "$(remove_wildcard "$product_service")" || true
  pkill -f "$(remove_wildcard "$recommendation_service")" || true
  pkill -f "$(remove_wildcard "$review_service")" || true
fi

echo "All services stopped."

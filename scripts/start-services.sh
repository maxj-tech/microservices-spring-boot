#!/bin/bash
# scripts/start-services.sh
# Script to manage starting services with optional configurations.
#
# Usage:
#   ./start-services.sh [options]
#
# Options:
#   --logging-level=<level>   Set logging level for Spring Boot applications.
#   --show-sql=<true/false>   Enable or disable SQL logging for JPA repositories.
#   docker                    Start services using Docker Compose.
#   clean                     Clean and build the project before starting services.
#   build                     Build the project before starting services.
#
# Example:
#   ./start-services.sh --logging-level=ERROR --show-sql=false docker
#
# Dependencies:
#   - Gradle wrapper 'gradlew' must be available in the project directory.
#   - Docker Compose must be installed and configured.
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

# Define default logging level parameter
LOGGING_LEVEL_PARAM=""
# Check for the logging-level flag and set the logging level parameter
if [[ $@ == *"--logging-level="* ]]; then
  LOGGING_LEVEL=$(echo "$@" | grep -oP '(?<=--logging-level=)[^ ]+')
  LOGGING_LEVEL_PARAM="--logging.level.root=$LOGGING_LEVEL \
                        --logging.level.org.springframework.web=$LOGGING_LEVEL \
                        --logging.level.tech.maxjung=$LOGGING_LEVEL \
                        --logging.level.org.hibernate=$LOGGING_LEVEL \
                        --logging.level.org.hibernate.SQL=$LOGGING_LEVEL \
                        --logging.level.org.springframework.data.mongodb.core.MongoTemplate=$LOGGING_LEVEL"
  echo "Logging level set to: $LOGGING_LEVEL"
fi

# Enable or disable SQL logging
SHOW_SQL_PARAM=""
if [[ $@ == *"--show-sql="* ]]; then
  VALUE=$(echo "$@" | grep -oP '(?<=--show-sql=)[^ ]+')
  SHOW_SQL_PARAM="--spring.jpa.show-sql=$VALUE"
  echo "SQL Logging: $VALUE"
fi

# Combine the parameters
PARAMS="$LOGGING_LEVEL_PARAM $SHOW_SQL_PARAM"
#echo "PARAMS: $PARAMS"



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
  docker compose up -d mysql mongodb
  java -jar $review_service $PARAMS & rev_pid=$!
  java -jar $product_service $PARAMS & prod_pid=$!
  java -jar $recommendation_service $PARAMS & rec_pid=$!
  java -jar $product_comp_service $PARAMS & comp_pid=$!
fi

echo "All services started successfully."

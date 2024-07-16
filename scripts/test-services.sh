#!/bin/bash
# scripts/test-services.sh
# Script to test services with configurable parameters and assertions.
# The script sets up test data, starts services (optionally cleaning and building),
# and performs various HTTP tests against configured endpoints.
#
# Usage:
#   ./test-services.sh [options]
#
# Options:
#   HOST=localhost            Specify the hostname or IP address of the service.
#   PORT=7000                 Specify the port number of the service.
#   PROD_ID_REVS_RECS=1       Default product ID with both reviews and recommendations.
#   PROD_ID_NOT_FOUND=13      Default product ID that does not exist.
#   PROD_ID_NO_RECS=113       Default product ID with no recommendations.
#   PROD_ID_NO_REVS=213       Default product ID with no reviews.
#   docker                    Start services using Docker Compose (changes default port to 8080).
#   PORT=<port_number>        Specify a custom port number for testing.
#   --logging-level=<level>   Set logging level for services like DEBUG, INFO, WARN, ERROR.
#                             If not set, the default logging level is used as configured in the services.
#   --show-sql=<true/false>   Enable or disable SQL logging for JPA repositories.
#                             If not set, the default value is used as configured in the services.
#   start [clean|build]       Start services before running tests, with optional 'clean' and 'build' parameters.
#   stop                      Stop services after running tests.
#
# Example:
#   ./test-services.sh --logging-level=ERROR --show-sql=false start
#
# Dependencies:
#   - curl must be installed and available in the PATH.
#   - jq must be installed to parse JSON responses.
#   - Docker Compose must be installed and configured to use Docker options.
#
set -e

# Determine the directory of the script
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
# Change to the script directory to ensure relative paths work
cd "$SCRIPT_DIR"

# Default variables
: ${HOST=localhost}
: ${PORT=7000}
: ${PROD_ID_REVS_RECS=1}
: ${PROD_ID_NOT_FOUND=13}
: ${PROD_ID_NO_RECS=113}
: ${PROD_ID_NO_REVS=213}

# Check for the "docker" flag and set the docker default port
docker=false
if [[ $@ == *"docker"* ]]; then
  docker=true
  PORT=8080
fi

# Check if the PORT is passed as a parameter and set it
if [[ $@ == *"PORT="* ]]; then
  PORT=$(echo "$@" | grep -oP 'PORT=\K\d+')
fi


# Function to assert the HTTP response code
function assertCurl() {
  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]; then
    if [ "$httpCode" = "200" ]; then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
    echo "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
    echo "- Failing command: $curlCmd"
    echo "- Response Body: $RESPONSE"
    exit 1
  fi
}

# Function to assert equality of values
function assertEqual() {
  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]; then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testUrl() {
  url=$@
  if $url -ks -f -o /dev/null
  then
    return 0
  else
    return 1
  fi;
}

function waitForService() {
  url=$@
  echo -n "Wait for: $url... "
  n=0
  until testUrl $url
  do
    n=$((n + 1))
    if [[ $n == 45 ]]
    then
      echo " Give up"
      exit 1
    else
      sleep 3
      echo -n ", retry #$n "
    fi
  done
  echo "DONE, continues..."
}

function recreateComposite() {
  local productId=$1
  local composite=$2

  assertCurl 200 "curl -X DELETE http://$HOST:$PORT/product-composite/${productId} -s"
  curl -X POST http://$HOST:$PORT/product-composite --json "$composite"
  echo "Recreated composite for productId: $productId"
}

function setupTestdata() {
  body="{\"productId\":$PROD_ID_NO_RECS"
  body+=\
',"name":"product name A","weight":100, "reviews":[
  {"reviewId":1,"author":"author 1","subject":"subject 1","content":"content 1"},
  {"reviewId":2,"author":"author 2","subject":"subject 2","content":"content 2"},
  {"reviewId":3,"author":"author 3","subject":"subject 3","content":"content 3"}
]}'
  recreateComposite "$PROD_ID_NO_RECS" "$body"

  body="{\"productId\":$PROD_ID_NO_REVS"
  body+=\
',"name":"product name B","weight":200, "recommendations":[
  {"recommendationId":1,"author":"author 1","rate":1,"content":"content 1"},
  {"recommendationId":2,"author":"author 2","rate":2,"content":"content 2"},
  {"recommendationId":3,"author":"author 3","rate":3,"content":"content 3"}
]}'
  recreateComposite "$PROD_ID_NO_REVS" "$body"


  body="{\"productId\":$PROD_ID_REVS_RECS"
  body+=\
',"name":"product name C","weight":300, "recommendations":[
      {"recommendationId":1,"author":"author 1","rate":1,"content":"content 1"},
      {"recommendationId":2,"author":"author 2","rate":2,"content":"content 2"},
      {"recommendationId":3,"author":"author 3","rate":3,"content":"content 3"}
  ], "reviews":[
      {"reviewId":1,"author":"author 1","subject":"subject 1","content":"content 1"},
      {"reviewId":2,"author":"author 2","subject":"subject 2","content":"content 2"},
      {"reviewId":3,"author":"author 3","subject":"subject 3","content":"content 3"}
  ]}'
  recreateComposite "$PROD_ID_REVS_RECS" "$body"
  echo "Test data setup done"

}

# Enable strict error handling
set -e


echo "Start Tests:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

# Define default logging level parameter
LOGGING_LEVEL_PARAM=""
# Check for the logging-level flag and set the logging level parameter
if [[ $@ == *"--logging-level="* ]]; then
  LOGGING_LEVEL=$(echo "$@" | grep -oP '(?<=--logging-level=)[^ ]+')
  LOGGING_LEVEL_PARAM="--logging-level=$LOGGING_LEVEL"
fi

# Enable or disable SQL logging
SHOW_SQL_PARAM=""
if [[ $@ == *"--show-sql="* ]]; then
  VALUE=$(echo "$@" | grep -oP '(?<=--show-sql=)[^ ]+')
  SHOW_SQL_PARAM="--show-sql=$VALUE"
fi

# Combine the parameters
PARAMS="$LOGGING_LEVEL_PARAM $SHOW_SQL_PARAM"
#echo "PARAMS: $PARAMS"

if [[ $@ == *"start"* ]]; then
  # Perform clean or build if requested
  if [[ $@ == *"clean"* ]]; then
    PARAMS="$PARAMS clean build"
  elif [[ $@ == *"build"* ]]; then
    PARAMS="$PARAMS build"
  fi

  "$SCRIPT_DIR/stop-services.sh"
  if [[ $docker == true ]]; then
     "$SCRIPT_DIR/start-services.sh" docker $PARAMS
  else
     "$SCRIPT_DIR/start-services.sh" $PARAMS || { echo "./start-services.sh failed"; exit 1; }
  fi
  sleep 15
fi

waitForService curl -X DELETE http://$HOST:$PORT/product-composite/$PROD_ID_NOT_FOUND

setupTestdata

# Happy Path Tests
echo -e "\n Verify that a normal request works, expect 3 recommendations and 3 reviews for productId $PROD_ID_REVS_RECS"
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS -s"
assertEqual $PROD_ID_REVS_RECS $(echo $RESPONSE | jq .productId)
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\n Verify that no recommendations are returned for productId $PROD_ID_NO_RECS"
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NO_RECS -s"
assertEqual $PROD_ID_NO_RECS $(echo $RESPONSE | jq .productId)
assertEqual 0 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\n Verify that no reviews are returned for productId $PROD_ID_NO_REVS"
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NO_REVS -s"
assertEqual $PROD_ID_NO_REVS $(echo $RESPONSE | jq .productId)
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 0 $(echo $RESPONSE | jq ".reviews | length")


echo -e "\n Running error case tests..."

echo -e "\n Verify that a 404 (Not Found) error is returned for a non-existing productId ($PROD_ID_NOT_FOUND)"
assertCurl 404 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NOT_FOUND -s"
assertEqual "No product found for productId: $PROD_ID_NOT_FOUND" "$(echo $RESPONSE | jq -r .message)"

echo -e "\n Verify that a 422 (Unprocessable Entity) error is returned for a productId that is out of range (-1)"
assertCurl 422 "curl http://$HOST:$PORT/product-composite/-1 -s"
assertEqual "\"Invalid productId: -1\"" "$(echo $RESPONSE | jq .message)"

echo -e "\n Verify that a 400 (Bad Request) error is returned for a productId that is not a number, i.e. invalid format"
assertCurl 400 "curl http://$HOST:$PORT/product-composite/invalidProductId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"


echo -e "\n Verify access to Swagger and OpenAPI URLs"
assertCurl 302 "curl -s  http://$HOST:$PORT/openapi/swagger-ui.html"
assertCurl 200 "curl -sL http://$HOST:$PORT/openapi/swagger-ui.html"
assertCurl 200 "curl -s  http://$HOST:$PORT/openapi/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config"
assertCurl 200 "curl -s  http://$HOST:$PORT/openapi/v3/api-docs"
assertEqual "3.0.1" "$(echo $RESPONSE | jq -r .openapi)"
assertEqual "http://$HOST:$PORT" "$(echo $RESPONSE | jq -r '.servers[0].url')"
assertCurl 200 "curl -s  http://$HOST:$PORT/openapi/v3/api-docs.yaml"


if [[ $@ == *"stop"* ]]
then
  "$SCRIPT_DIR/stop-services.sh"
fi

echo "End, all tests OK:" `date`

# Microservices with Spring Boot

This document provides helpful hints, reminders, and bash snippets for managing microservices built with Spring Boot and Spring Cloud.

## General Commands

### Build and Clean
- **Build all services**: `./gradlew build`
- **Clean all services**: `./gradlew clean`

### Manage Services Individually
To run a specific service from the project root, use the commands below for each service:

#### Product Composite Service
- **Start service**: `./gradlew :services:product-comp-service:bootRun`
- **Clean, build, and start service**: `./gradlew :services:product-comp-service:clean :services:product-comp-service:build :services:product-comp-service:bootRun`

#### Product Service
- **Start service**: `./gradlew :services:product-service:bootRun`
- **Clean, build, and start service**: `./gradlew :services:product-service:clean :services:product-service:build :services:product-service:bootRun`

#### Recommendation Service
- **Start service**: `./gradlew :services:recommendation-service:bootRun`
- **Clean, build, and start service**: `./gradlew :services:recommendation-service:clean :services:recommendation-service:build :services:recommendation-service:bootRun`

#### Review Service
- **Start service**: `./gradlew :services:review-service:bootRun`
- **Clean, build, and start service**: `./gradlew :services:review-service:clean :services:review-service:build :services:review-service:bootRun`

## Script Management
Scripts are located in the `/scripts/` folder. To make these scripts executable, run:
```bash
chmod +x ./scripts/*.sh
```

### Start Services
To start all services as background processes:
```bash
./scripts/start-services.sh
```
Optional arguments for performing a Gradle clean or build before starting the services:
```bash
./scripts/start-services.sh clean
```
```bash
./scripts/start-services.sh build
```
To start all services using Docker Compose:
```bash
./scripts/start-services.sh docker
```


### Test Services
To perform curl requests to the composite service that talks to all other services:
- default port 8080 (docker):
  ```bash
  ./scripts/test-services.sh
  ```
- port 7000 (dev):
  ```bash
  PORT=7000 ./scripts/test-services.sh
  ```
- default port 8080 (docker) plus starting and stopping:
  ```bash
  ./scripts/test-services.sh start stop
  ```

### Stop Services
To stop all running services:
```bash
./scripts/stop-services.sh
```

## Using IntelliJ HTTP Client with Environment Files

### Setting Up Environment Files

Place the environment and HTTP request files in the `/http-requests/` directory. Example structure:

```plaintext
/http-requests/
  ├── happy-path.http
  ├── error-handling.http
  ├── http-client.env.json
  ├── http-client.private.env.json  # Private environment configuration
```

### Environment Files

#### http-client.env.json

- **Purpose**: Defines different environment configurations for your HTTP requests.
- **Usage**: Contains key-value pairs for environment variables such as `host`, `port_product_composite`, ...

#### http-client.private.env.json

- **Purpose**: Stores sensitive environment configurations that should not be exposed publicly (e.g., personal API keys, private URLs).
- **Usage**: Similar to `http-client.env.json`, but used for private settings.
- **Important**: Ensure this file is added to your `.gitignore` to prevent it from being committed to the repository.


### HTTP Request Files

- **Purpose**: Contains HTTP requests to test various endpoints of your microservices.
- **Example Files**:
  - `happy-path.http`: Tests for typical use cases where services are expected to return successful responses.
  - `error-handling.http`: Tests for error scenarios to ensure services handle invalid inputs gracefully.
- **Usage**: Requests in these files use placeholders (e.g., `{{host}}`, `{{port_product_composite}}`) that are replaced with values from the selected environment in `http-client.env.json` or `http-client.private.env.json`.

### Switching Between Environments

- Open the HTTP request file in IntelliJ IDEA.
- Use the environment dropdown at the top-right corner of the HTTP request editor to select the desired environment (`dev`, `docker`, etc.).



## Setup Development Environment
- **Java Installation**: Use SDKMAN to install Java. Recommended version is Java 17 from JetBrains (`-jbr`).
- **Docker**: TODO

## Handy Docker Commands
- 💡Pipe the output of `docker ps -aq` (which lists all container IDs) into the `docker rm` command, 
  which **removes all containers** listed using the`-f` force flag:
    ```bash
    docker rm -f $(docker ps -aq)
    ```

- print all data stored in databases
  ```bash
  docker compose exec mongodb mongosh product-db --quiet --eval "db.products.find()";
  docker compose exec mongodb mongosh recommendation-db --quiet --eval "db.recommendations.find()";
  docker compose exec mysql mysql -uuser -p review-db -e "select * from reviews";
  ```
- clear all databases
    ```bash
    docker compose exec mongodb mongosh product-db --quiet --eval "db.products.drop()"
    docker compose exec mongodb mongosh recommendation-db --quiet --eval "db.recommendations.drop()"
    docker compose exec mysql mysql -uuser -p review-db -e "delete from reviews"
    ```


### TODO
- If you are using IntelliJ IDEA, you also need to ensure that support for annotation processing is enabled. 
  Open Preferences and navigate to Build, Execute, Deployment | Compiler | Annotations Processors. 
  Verify that the checkbox named Enable annotation processing is selected!

- **run all *PersistenceTests** with gradle:
    ```bash
  ./gradlew :services:product-service:test --tests '*PersistenceTests'
  ./gradlew :services:recommendation-service:test --tests '*PersistenceTests'
  ./gradlew :services:review-service:test --tests '*PersistenceTests'
    ```
- update swagger ui
  ```bash
  ./gradlew :services:product-service:generateSwaggerDocumentation
  ./gradlew :services:recommendation-service:generateSwaggerDocumentation
  ./gradlew :services:review-service:generateSwaggerDocumentation
  ./gradlew :services:product-comp-service:generateSwaggerDocumentation
  ```
- [view swagger ui](http://localhost:8080/openapi/swagger-ui.html)
- [view OpenAPI document](http://localhost:8080/openapi/v3/api-docs)

- build from scratch and start everything
  ```bash
  ./gradlew clean build && docker compose build --no-cache && docker compose up -d
  ```

- build from scratch , start and TEST everything then shutdown
  ```bash
   ./gradlew clean build && docker compose build --no-cache && ./scripts/test-services.sh start stop && ./scripts/start-services.sh && PORT=7000 ./scripts/test-services.sh && ./scripts/stop-services.sh
  ```
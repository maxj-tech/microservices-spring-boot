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


## Setup Development Environment
- **Java Installation**: Use SDKMAN to install Java. Recommended version is Java 17 from JetBrains (`-jbr`).
- **Docker**: TODO

## Handy Docker Commands
- 💡Pipe the output of `docker ps -aq` (which lists all container IDs) into the `docker rm` command, 
  which **removes all containers** listed using the`-f` force flag:
    ```bash
    docker rm -f $(docker ps -aq)
    ```

  

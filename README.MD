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

### Test Services
To perform curl requests to the main endpoints of each service to check their normal operation:
```bash
./scripts/test-services.sh
```

### Stop Services
To stop all running services:
```bash
./scripts/stop-services.sh
```

## Curl Commands for Testing Services
### Happy Paths
- **Product Composite Service**: `curl http://localhost:7000/product-composite/123 -s | jq .`
- **Product Service**: `curl http://localhost:7001/product/123 -s | jq .`
- **Recommendation Service**: `curl http://localhost:7002/recommendation?productId=123 -s | jq .`
- **Review Service**: `curl http://localhost:7003/review?productId=123 -s | jq .`

### Error Handling Tests
- **Expect 404 from Product Composite Service**: `curl http://localhost:7000/product-composite/13 -i; echo`
- **Expect 400 from Product Service**: `curl http://localhost:7001/product/invalidProductId -i; echo`
- **No results from Recommendation Service**: `curl http://localhost:7002/recommendation?productId=113 -s | jq .`
- **No results from Review Service**: `curl http://localhost:7003/review?productId=213 -s | jq .`

## Setup Development Environment
- **Java Installation**: Use SDKMAN to install Java. Recommended version is Java 17 from JetBrains (`-jbr`).

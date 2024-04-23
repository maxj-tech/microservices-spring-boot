# Microservices with Spring Boot and Spring Cloud

## Helpful hints and reminders

### Clean, build, run single services
Since this is a **multi-project** gradle project, if you want to run a specific service, run this command from the
project root:

- product-service:
    - `./gradlew :services:product-service:bootRun`
    - `./gradlew :services:product-service:clean :services:product-service:build :services:product-service:bootRun`

- recommendation-service:
    - `./gradlew :services:recommendation-service:bootRun`
    - `./gradlew :services:recommendation-service:clean :services:recommendation-service:build :services:recommendation-service:bootRun`

- review-service:
    - `./gradlew :services:review-service:bootRun`
    - `./gradlew :services:review-service:clean :services:review-service:build :services:review-service:bootRun`


### Curl single services
- product-service:  `curl http://localhost:7001/product/123`
- recommendation-service:  `curl http://localhost:7002/recommendation?productId=123`
- review-service:  `curl http://localhost:7003/review?productId=123`



# Microservices with Spring Boot and Spring Cloud

## Helpful hints and reminders
Since this is a **multi-project** gradle project, if you want to run a specific service, run this command from the
project root:

- `product-service`:
    - `./gradlew :services:product-service:bootRun`
    - `./gradlew :services:product-service:clean :services:product-service:build :services:product-service:bootRun`

- `recommendation-service`:
    - `./gradlew :services:recommendation-service:bootRun`
    - `./gradlew :services:recommendation-service:clean :services:recommendation-service:build :services:recommendation-service:bootRun`

- `review-service`:
    - `./gradlew :services:review-service:bootRun`
    - `./gradlew :services:review-service:clean :services:review-service:build :services:review-service:bootRun`




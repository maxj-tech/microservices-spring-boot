plugins {
    java

    /*
     * `apply false`: Includes the plugin without activating its configurations.
     * Used to utilize the plugin’s dependency management capabilities
     * without applying its Spring Boot-specific configurations to the project.
     */
    id("org.springframework.boot") version "3.2.5" apply false
    id("io.spring.dependency-management") version "1.1.4"
}

val springBootVersion: String by extra("3.2.5")

group = "tech.maxjung.microservices"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

// Configuring dependency management to import BOM
dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${springBootVersion}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

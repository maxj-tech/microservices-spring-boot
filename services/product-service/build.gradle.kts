plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "tech.maxjung.microservices.core"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

val mapstructVersion = "1.5.5.Final"

repositories {
    mavenCentral()
}

// we don't need to create a plain jar file
// see https://docs.spring.io/spring-boot/docs/3.2.5/gradle-plugin/reference/htmlsingle/#packaging-executable.and-plain-archives
tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":api"))
    implementation(project(":util"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")

    compileOnly("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    implementation(platform("org.testcontainers:testcontainers-bom:1.19.8"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

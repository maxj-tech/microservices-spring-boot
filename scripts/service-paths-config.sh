# scripts/service-paths-config.sh
# path to service JARs.
# RELIES on the setting in the build.gradle.kts files not to produce a plain JAR file.
export product_comp_service="services/product-comp-service/build/libs/*.jar"
export product_service="services/product-service/build/libs/*.jar"
export recommendation_service="services/recommendation-service/build/libs/*.jar"
export review_service="services/review-service/build/libs/*.jar"

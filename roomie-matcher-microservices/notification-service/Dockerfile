FROM maven:3.8.6-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY ./common-lib ./common-lib
COPY ./notification-service ./notification-service

# Build all modules (common-lib first, then notification-service)
RUN mvn -B -f common-lib/pom.xml clean install -DskipTests
RUN mvn -B -f notification-service/pom.xml clean package -DskipTests

# Final image
FROM eclipse-temurin:17.0.5_8-jre-alpine
WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /app/notification-service/target/*.jar /app/notification-service.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose application port
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"] 
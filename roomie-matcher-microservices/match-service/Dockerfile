FROM maven:3.8.6-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY ./common-lib ./common-lib
COPY ./match-service ./match-service

# Build all modules (common-lib first, then match-service)
RUN mvn -B -f common-lib/pom.xml clean install -DskipTests
RUN mvn -B -f match-service/pom.xml clean package -DskipTests

# Final image
FROM eclipse-temurin:17.0.5_8-jre-alpine
WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /app/match-service/target/*.jar /app/match-service.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose application port
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "/app/match-service.jar"] 
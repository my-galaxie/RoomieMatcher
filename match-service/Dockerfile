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

# Install curl for health check
RUN apk add --no-cache curl

# Copy built artifact from builder stage
COPY --from=builder /app/match-service/target/*.jar /app/match-service.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expose application port
EXPOSE 8083

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/match-service.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/api/v1/actuator/health || exit 1 
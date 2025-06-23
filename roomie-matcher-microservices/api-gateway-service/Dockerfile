FROM maven:3.8.6-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY ./api-gateway-service ./api-gateway-service

# Build the API Gateway
RUN mvn -B -f api-gateway-service/pom.xml clean package -DskipTests

# Final image
FROM eclipse-temurin:17.0.5_8-jre-alpine
WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /app/api-gateway-service/target/*.jar /app/api-gateway.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"] 
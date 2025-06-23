FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copy maven files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY notification-service/pom.xml notification-service/pom.xml
COPY common-lib/pom.xml common-lib/pom.xml

# Make the mvnw script executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B -f notification-service/pom.xml

# Copy source code
COPY common-lib/src common-lib/src
COPY notification-service/src notification-service/src

# Build the application
RUN ./mvnw clean package -DskipTests -f notification-service/pom.xml

# Create a lightweight container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install curl for health check
RUN apk add --no-cache curl

# Copy the built artifact
COPY --from=build /workspace/app/notification-service/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Set the entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Expose the port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8084/api/v1/actuator/health || exit 1 
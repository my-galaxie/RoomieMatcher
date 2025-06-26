FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copy maven files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY profile-service/pom.xml profile-service/pom.xml
COPY common-lib/pom.xml common-lib/pom.xml

# Make the mvnw script executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B -f profile-service/pom.xml

# Copy source code
COPY common-lib/src common-lib/src
COPY profile-service/src profile-service/src

# Build the application
RUN ./mvnw clean package -DskipTests -f profile-service/pom.xml

# Create a lightweight container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install curl for health check
RUN apk add --no-cache curl

# Copy the built artifact
COPY --from=build /workspace/app/profile-service/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms96m -Xmx128m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -Xss256k"

# Set the entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Expose the port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=60s --timeout=3s --start-period=120s --retries=3 \
  CMD curl -f http://localhost:8082/api/v1/actuator/health || exit 1 
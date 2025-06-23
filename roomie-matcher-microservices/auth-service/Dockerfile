FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copy the parent pom.xml and module directories
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY common-lib/ ./common-lib/
COPY auth-service/pom.xml ./auth-service/

# Maven build to download dependencies for caching
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B -f auth-service/pom.xml

# Copy source code
COPY auth-service/src ./auth-service/src

# Build the project
RUN ./mvnw clean package -DskipTests -f auth-service/pom.xml

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/auth-service/target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"] 
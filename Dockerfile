# Use a lightweight Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Where the app will live inside the container
WORKDIR /app

# Copy Maven wrapper and pom first (better Docker caching)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (so they’re cached in Docker layers)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the jar
RUN ./mvnw package -DskipTests

# Spring Boot default port
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "target/airbnbclean-0.0.1-SNAPSHOT.jar"]

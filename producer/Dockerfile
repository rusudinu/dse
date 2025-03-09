# Build stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

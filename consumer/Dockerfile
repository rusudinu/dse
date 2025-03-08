# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=75 -Dspring.jmx.enabled=false"
ENTRYPOINT ["java", "-jar", "app.jar"] 

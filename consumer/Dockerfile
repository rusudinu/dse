FROM gradle:8.12-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon --stacktrace

FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

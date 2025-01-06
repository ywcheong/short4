FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=build/docker/app.jar

COPY ${JAR_FILE} /app/server.jar
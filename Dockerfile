#FROM openjdk:17-jdk-slim
#WORKDIR /app
#COPY app.jar app.jar
#COPY .env .env
#ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.import=optional:file:.env[.properties]"]


# Build Stage
FROM amazoncorretto:17-alpine AS jar_builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build

# Run Stage
FROM gcr.io/distroless/java17-debian11 AS jre_builder
WORKDIR /app
COPY app.jar app.jar
ENTRYPOINT ["sh", "-c", "mkdir -p /app/logs && java -jar app.jar >> /app/logs/app.log 2>&1"]
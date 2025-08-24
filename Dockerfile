#FROM openjdk:17-jdk-slim
#WORKDIR /app
#COPY app.jar app.jar
#COPY .env .env
#ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.import=optional:file:.env[.properties]"]
#

## Build Stage
#FROM amazoncorretto:17-alpine AS jar_builder
#WORKDIR /app
#COPY . .
#RUN chmod +x ./gradlew
#RUN ./gradlew clean bootjar

# Run Stage
FROM gcr.io/distroless/java17-debian11
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
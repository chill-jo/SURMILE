FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/app.jar app.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.import=optional:file:.env[.properties]"]
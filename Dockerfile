FROM openjdk:17-jdk-slim
WORKDIR /app
COPY app.jar app.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar"]

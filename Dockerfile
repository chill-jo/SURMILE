FROM gcr.io/distroless/java17-debian11 AS jre_builder
WORKDIR /app
COPY build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
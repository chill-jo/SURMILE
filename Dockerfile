## Build Stage
#FROM gradle:7.6-jdk17 AS jar_builder
#WORKDIR /app
#COPY . .
#RUN chmod +x ./gradlew
#RUN ./gradlew clean build jacocoTestReport

# Run Stage
FROM gcr.io/distroless/java17-debian11 AS jre_builder
WORKDIR /app
#COPY --from=jar_builder /app/build/libs/*SNAPSHOT.jar app.jar
COPY build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
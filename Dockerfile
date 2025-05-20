# Stage 1: build project
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: chạy ứng dụng
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/Online-Food-Ordering-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

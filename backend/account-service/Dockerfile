# Simple runtime-only image. Build the jar first from the project root with:
#   mvn clean package -DskipTests
# then run docker-compose up --build (or docker build this folder directly).
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

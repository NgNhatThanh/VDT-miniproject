FROM eclipse-temurin:21-jre-alpine
COPY target/document-service*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
FROM eclipse-temurin:21-jre-alpine
COPY target/service-discovery*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
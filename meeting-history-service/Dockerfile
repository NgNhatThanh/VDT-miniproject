FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy và build common-lib trước
COPY ../common-lib ./common-lib
RUN mvn -f common-lib/pom.xml clean install -DskipTests

# Copy service-a code
WORKDIR /build/meeting-history-service
COPY meeting-history-service/pom.xml /build/meeting-history-service
COPY meeting-history-service/src /build/meeting-history-service/src

# Build service-a (lúc này đã có common-lib trong local .m2)
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/meeting-history-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]
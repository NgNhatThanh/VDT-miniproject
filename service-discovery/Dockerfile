#FROM maven:3.9.9-eclipse-temurin-21 AS build
#WORKDIR /app
#COPY pom.xml /app
#RUN mvn dependency:go-offline
#COPY src /app/src
#RUN mvn clean package -DskipTests "-Dspring.profiles.active=docker"
#
#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy và build common-lib trước
COPY ../common-lib ./common-lib
RUN mvn -f common-lib/pom.xml clean install -DskipTests

# Copy service-a code
WORKDIR /build/service-discovery
COPY service-discovery/pom.xml /build/service-discovery
COPY service-discovery/src /build/service-discovery/src

# Build service-a (lúc này đã có common-lib trong local .m2)
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/service-discovery/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]
#FROM maven:3.9.9-eclipse-temurin-21 AS build
#WORKDIR /app
#COPY pom.xml /app
#COPY ../common-lib/pom.xml /common-lib/
#RUN mvn dependency:go-offline
#COPY src /app/src
#COPY ../common-lib/src /common-lib/src
#RUN mvn clean package -DskipTests "-Dspring.profiles.active=docker"
#
#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/common-lib/target/*.jar /app/libs/common-lib.jar
#COPY --from=build /app/target/*.jar app.jar
#ENTRYPOINT ["sh", "-c", "echo 'Waiting for Databases...'; sleep 30; exec java -jar -Dloader.path=/app/libs/ app.jar --spring.profiles.active=docker"]

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy và build common-lib trước
COPY ../common-lib ./common-lib
RUN mvn -f common-lib/pom.xml clean install -DskipTests

# Copy service-a code
WORKDIR /build/vote-service
COPY vote-service/pom.xml /build/vote-service
COPY vote-service/src /build/vote-service/src

# Build service-a (lúc này đã có common-lib trong local .m2)
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/vote-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]
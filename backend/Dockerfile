# Multi-stage build for production-ready Spring Boot image
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S daip && adduser -S daip -G daip
RUN mkdir -p /app/uploads && chown -R daip:daip /app
COPY --from=build /app/target/*.jar app.jar
USER daip
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

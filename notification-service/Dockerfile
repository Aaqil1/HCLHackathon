FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY notification-service/pom.xml ./notification-service/
COPY notification-service/src ./notification-service/src
RUN mvn clean package -DskipTests -pl notification-service -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]


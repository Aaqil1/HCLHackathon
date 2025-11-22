FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY payment-service/pom.xml ./payment-service/
COPY payment-service/src ./payment-service/src
RUN mvn clean package -DskipTests -pl payment-service -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]


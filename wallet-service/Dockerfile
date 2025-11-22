FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY wallet-service/pom.xml ./wallet-service/
COPY wallet-service/src ./wallet-service/src
RUN mvn clean package -DskipTests -pl wallet-service -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/wallet-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]


FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY sever/ sever/
RUN mvn -f sever/pom.xml package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/sever/target/auction-server-1.0-SNAPSHOT.jar app.jar
EXPOSE 5000
CMD ["java", "-jar", "app.jar"]

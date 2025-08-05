# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app

# Copiamos solo pom.xml para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copiamos el código y compilamos rápido
COPY src ./src
RUN mvn package -DskipTests -B

# ---- Runtime stage ----
FROM azul/zulu-openjdk:24-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

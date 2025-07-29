FROM azul/zulu-openjdk:24-jre

WORKDIR /app

COPY target/forohub-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

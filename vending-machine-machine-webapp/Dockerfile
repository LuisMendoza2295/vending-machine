FROM openjdk:8-alpine

COPY target/machine-app.jar machine-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/machine-app.jar"]
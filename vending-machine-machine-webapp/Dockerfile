FROM open-jdk:8-jdk-alpine

COPY target/machine-app.jar machine-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/machine-app.jar"]
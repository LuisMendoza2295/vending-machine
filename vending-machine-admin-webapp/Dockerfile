FROM openjdk:8-alpine

COPY target/admin-app.jar admin-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/admin-app.jar"]
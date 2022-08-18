FROM openjdk:16-jdk-alpine
VOLUME /main-app
ADD build/libs/ecollege-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]

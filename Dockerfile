FROM openjdk:8-jdk-alpine
MAINTAINER Atonu
COPY target/bootcamp-0.0.1-SNAPSHOT.jar bootcamp.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","/bootcamp.jar"]

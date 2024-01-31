FROM openjdk:17
MAINTAINER Shahbaz Hussain
COPY target/starling-round-up-saving-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

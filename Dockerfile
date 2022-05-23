# For Java 8, try this
# FROM openjdk:8-jdk-alpine

# For Java 11, try this
FROM openjdk:11-jre-slim

RUN apt-get update; apt-get install -y fontconfig libfreetype6

# Refer to Maven build -> finalName
ARG JAR_FILE=target/jirareports-0.0.1-SNAPSHOT.jar

# Refer to Maven build -> finalName
ARG PROP_FILE=jirareport.properties

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar
COPY ${PROP_FILE} main.properties

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar", "--spring.config.location=main.properties"]
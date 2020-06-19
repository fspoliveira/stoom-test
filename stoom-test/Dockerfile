FROM openjdk:8-jre-slim

LABEL maintainer="Stoom"
LABEL version="1.0"

ENV DB_URL="jdbc:postgresql://localhost:5432/postgres?loggerLevel=OFF"
ENV DB_USER="stoom"
ENV DB_PASS="stoom"
ENV REDIS_HOST="localhost"
ENV REDIS_PORT="6379"

EXPOSE 8080

COPY target/stoom-test.jar stoom-test.jar

ENTRYPOINT [ "java" , "-Xms128m",  "-Xmx400m", "-jar", "-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector", "stoom-test.jar"]
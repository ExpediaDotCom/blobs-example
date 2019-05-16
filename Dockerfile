FROM openjdk:8-jre
MAINTAINER Haystack <haystack@expedia.com>

ENV APP_NAME blobExample-service-1.0-SNAPSHOT
ENV APP_HOME /app/bin

RUN mkdir -p ${APP_HOME}
RUN mkdir -p ${APP_HOME}/JMH-BenchmarkingResults
RUN chmod 777 ${APP_HOME}/JMH-BenchmarkingResults

COPY /target/${APP_NAME}.jar ${APP_HOME}/

WORKDIR ${APP_HOME}

ENTRYPOINT ["java", "-jar", "blobExample-service-1.0-SNAPSHOT.jar", "benchmark"]
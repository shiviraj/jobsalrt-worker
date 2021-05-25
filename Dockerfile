FROM gradle:7.0.2-jdk11 AS BUILDER
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle.kts settings.gradle.kts $APP_HOME
COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src
RUN gradle build || return 0
COPY . .
RUN gradle clean build

# actual container
FROM adoptopenjdk/openjdk11:alpine-jre
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME
COPY --from=BUILDER $APP_HOME/build/libs/*.jar .

ENTRYPOINT ["java", "-jar", ".*.jar"]


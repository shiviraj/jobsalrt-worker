FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /usr/src/app

COPY /build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]


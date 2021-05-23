FROM adoptopenjdk/openjdk11

WORKDIR /home/workspace

RUN ./gradlew clean build

COPY build/libs/*.jar .

ENTRYPOINT ["java", "-jar", "*.jar"]

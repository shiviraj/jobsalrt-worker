FROM adoptopenjdk/openjdk11

WORKDIR /home/workspace

COPY build/libs/*.jar .

ENTRYPOINT ["java", "-jar", "*.jar"]

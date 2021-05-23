FROM adoptopenjdk/openjdk11

WORKDIR /home/workspace

ARG DEPENDENCY=build/dependency

RUN bash -c 'echo -e ${DEPENDENCY}'

# Dependencies
COPY ${DEPENDENCY}/BOOT-INF/lib app/lib

COPY ${DEPENDENCY}/META-INF app/META-INF

# Project Code
COPY ${DEPENDENCY}/BOOT-INF/classes app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.jobsalrt.worker.WorkerApplication"]

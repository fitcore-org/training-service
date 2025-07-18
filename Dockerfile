FROM openjdk:17-jdk-slim

WORKDIR /app

COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

EXPOSE 8082

CMD ["java", "-jar", "build/libs/training-service-0.0.1-SNAPSHOT.jar"]

FROM eclipse-temurin:21-jdk-jammy
LABEL authors="nipin"
WORKDIR /app
COPY build/libs/password-rule-engine-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080
ENV SCRIPT_PATH=/app/scripts
ENTRYPOINT ["java", "-jar","password-rule-engine-0.0.1-SNAPSHOT.jar"]
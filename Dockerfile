FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/Info-Refineria-0.0.1.jar
COPY ${JAR_FILE} Info-Refineria.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Info-Refineria.jar"]

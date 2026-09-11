FROM eclipse-temurin:25-jre

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} questionservice.jar

ENTRYPOINT ["java", "-jar", "questionservice.jar"]
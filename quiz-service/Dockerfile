FROM eclipse-temurin:25-jre

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} quizservice.jar

ENTRYPOINT ["java", "-jar", "quizservice.jar"]
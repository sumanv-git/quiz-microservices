FROM eclipse-temurin:25-jre

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} apigateway.jar

ENTRYPOINT ["java", "-jar", "apigateway.jar"]
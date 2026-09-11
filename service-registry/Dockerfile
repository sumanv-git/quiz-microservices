FROM eclipse-temurin:25-jre

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} serviceregistry.jar

ENTRYPOINT ["java", "-jar", "serviceregistry.jar"]
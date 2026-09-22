# Quiz Microservices Design

```mermaid
flowchart LR
    User["User / Client<br/>Browser, Postman, Bruno, Frontend"]

    subgraph QuizSystem["Quiz Microservices System"]
        Gateway["API Gateway<br/>api-gateway<br/>Spring Cloud Gateway<br/>Port: 8765"]

        Registry["Service Registry<br/>service-registry<br/>Netflix Eureka Server<br/>Port: 8761"]

        QuestionService["Question Service<br/>question-service<br/>Spring Boot REST API<br/>Port: 9041<br/><br/>Responsibilities:<br/>- Manage questions<br/>- Fetch questions by category<br/>- Provide random question IDs<br/>- Calculate score"]

        QuizService["Quiz Service<br/>quiz-service<br/>Spring Boot REST API<br/>Port: 9042<br/><br/>Responsibilities:<br/>- Create quiz<br/>- Store quiz metadata<br/>- Fetch quiz questions<br/>- Submit quiz"]

        Postgres["PostgreSQL<br/>postgres-dev<br/>Port: 9032"]

        QuestionDB[("qustiondb<br/>Question database")]
        QuizDB[("quizdb<br/>Quiz database")]
    end

    User -->|"HTTP API calls"| Gateway

    Gateway -->|"Routes /question/**"| QuestionService
    Gateway -->|"Routes /quiz/**"| QuizService

    Gateway -.->|"Registers / discovers services"| Registry
    QuestionService -.->|"Registers with Eureka"| Registry
    QuizService -.->|"Registers with Eureka"| Registry

    QuizService -->|"OpenFeign REST call<br/>QuestionInterface<br/>@FeignClient(name='QUESTION-SERVICE')"| QuestionService

    QuestionService -->|"Spring Data JPA"| QuestionDB
    QuizService -->|"Spring Data JPA"| QuizDB

    Postgres --- QuestionDB
    Postgres --- QuizDB
```

## Code References

- `ServiceRegistryApplication`: `service-registry/src/main/java/com/learning/service_registry/ServiceRegistryApplication.java`
- `ApiGatewayApplication`: `api-gateway/src/main/java/com/learning/api_gateway/ApiGatewayApplication.java`
- `QuestionController`: `question-service/src/main/java/com/learning/question/controller/QuestionController.java`
- `QuizController`: `quiz-service/src/main/java/com/learning/quiz/controller/QuizController.java`
- `QuestionInterface`: `quiz-service/src/main/java/com/learning/quiz/feign/QuestionInterface.java`
- Runtime setup: `docker-compose.yaml`
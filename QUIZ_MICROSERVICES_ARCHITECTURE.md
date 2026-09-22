# Quiz Microservices Architecture

This document describes the current microservices architecture for the Quiz application.

## Architecture Diagram

```mermaid
flowchart LR
    Client["Client<br/>Browser / Postman / Frontend"]

    subgraph Platform["Quiz Microservices Platform"]
        Gateway["API Gateway<br/>api-gateway<br/>Spring Cloud Gateway<br/>Port: 8765<br/>ApiGatewayApplication"]

        Registry["Service Registry<br/>service-registry<br/>Netflix Eureka Server<br/>Port: 8761<br/>ServiceRegistryApplication<br/>@EnableEurekaServer"]

        subgraph Services["Business Services"]
            QuestionService["Question Service<br/>question-service<br/>Port: 9041<br/>QuestionServiceApplication<br/><br/>QuestionController<br/>QuestionService<br/>QuestionRepository"]

            QuizService["Quiz Service<br/>quiz-service<br/>Port: 9042<br/>QuizServiceApplication<br/>@EnableFeignClients<br/><br/>QuizController<br/>QuizService<br/>QuizRepository"]
        end

        subgraph Database["PostgreSQL: postgres-dev<br/>Port: 9032 -> 5432"]
            QuestionDB[("qustiondb<br/>question table")]
            QuizDB[("quizdb<br/>quiz data")]
        end
    end

    Client -->|"HTTP requests"| Gateway

    Gateway -->|"Route to /question/**"| QuestionService
    Gateway -->|"Route to /quiz/**"| QuizService

    Gateway -.->|"Register / discover"| Registry
    QuestionService -.->|"Register with Eureka"| Registry
    QuizService -.->|"Register with Eureka"| Registry

    QuizService -->|"OpenFeign REST call<br/>QuestionInterface<br/>@FeignClient(name='QUESTION-SERVICE')"| QuestionService

    QuestionService -->|"Spring Data JPA"| QuestionDB
    QuizService -->|"Spring Data JPA"| QuizDB
```

## Request Flow: Create Quiz

```mermaid
sequenceDiagram
    actor Client
    participant Gateway as API Gateway<br/>api-gateway:8765
    participant QuizController as QuizController.createQuiz
    participant QuizService as QuizService.createQuiz
    participant Feign as QuestionInterface.getQuestionsForQuiz
    participant QuestionController as QuestionController.getQuestionsForQuiz
    participant QuestionService as QuestionService.getQuestionsForQuiz
    participant QuestionRepository as QuestionRepository.findRandonQuestionsByCategory
    participant QuestionDB as PostgreSQL qustiondb
    participant QuizDB as PostgreSQL quizdb

    Client->>Gateway: GET /quiz/create?category=&numQ=&title=
    Gateway->>QuizController: Forward request
    QuizController->>QuizService: createQuiz(category, numQ, title)
    QuizService->>Feign: getQuestionsForQuiz(category, numQ)
    Feign->>QuestionController: GET /question/getQuestionsForQuiz
    QuestionController->>QuestionService: getQuestionsForQuiz(category, numQuestions)
    QuestionService->>QuestionRepository: findRandonQuestionsByCategory(category, numQ)
    QuestionRepository->>QuestionDB: Select random questions
    QuestionDB-->>QuestionRepository: Question rows
    QuestionRepository-->>QuestionService: Questions
    QuestionService-->>QuestionController: Question IDs
    QuestionController-->>Feign: List<Integer>
    Feign-->>QuizService: Question IDs
    QuizService->>QuizDB: Save Quiz
    QuizDB-->>QuizService: Quiz ID
    QuizService-->>QuizController: Quiz ID
    QuizController-->>Gateway: 201 Created
    Gateway-->>Client: Quiz ID
```

## Request Flow: Get Quiz Details

```mermaid
sequenceDiagram
    actor Client
    participant Gateway as API Gateway<br/>api-gateway:8765
    participant QuizController as QuizController.getQuizQuestions
    participant QuizService as QuizService.getAllQuestions
    participant QuizRepository as QuizRepository.findById
    participant QuizDB as PostgreSQL quizdb
    participant Feign as QuestionInterface.getQuestionsFromIds
    participant QuestionController as QuestionController.getQuestionsFromIds
    participant QuestionService as QuestionService.getQuestionsByIds
    participant QuestionDB as PostgreSQL qustiondb

    Client->>Gateway: GET /quiz/getDetails/{id}
    Gateway->>QuizController: Forward request
    QuizController->>QuizService: getAllQuestions(id)
    QuizService->>QuizRepository: findById(id)
    QuizRepository->>QuizDB: Load quiz
    QuizDB-->>QuizRepository: Quiz with question IDs
    QuizRepository-->>QuizService: Quiz
    QuizService->>Feign: getQuestionsFromIds(questionIds)
    Feign->>QuestionController: POST /question/getQuestionsByIds
    QuestionController->>QuestionService: getQuestionsByIds(questionIds)
    QuestionService->>QuestionDB: Load questions by IDs
    QuestionDB-->>QuestionService: Questions
    QuestionService-->>QuestionController: List<QuestionResponse>
    QuestionController-->>Feign: List<QuestionResponse>
    Feign-->>QuizService: Questions
    QuizService-->>QuizController: Questions
    QuizController-->>Gateway: 200 OK
    Gateway-->>Client: Questions
```

## Request Flow: Submit Quiz

```mermaid
sequenceDiagram
    actor Client
    participant Gateway as API Gateway<br/>api-gateway:8765
    participant QuizController as QuizController.quizSubmit
    participant QuizService as QuizService.submitQuiz
    participant QuizRepository as QuizRepository.findById
    participant QuizDB as PostgreSQL quizdb
    participant QuestionScore as QuestionInterface.getScore / QuestionController.getScore

    Client->>Gateway: POST /quiz/submitQuiz/{id}
    Gateway->>QuizController: Forward request
    QuizController->>QuizService: submitQuiz(id, quizResponses)
    QuizService->>QuizRepository: findById(id)
    QuizRepository->>QuizDB: Load quiz
    QuizDB-->>QuizRepository: Quiz
    QuizRepository-->>QuizService: Quiz
    Note over QuizService: Current code returns 0
    Note over QuestionScore: Scoring endpoint exists but is not called by QuizService.submitQuiz
    QuizService-->>QuizController: score = 0
    QuizController-->>Gateway: 200 OK
    Gateway-->>Client: score
```

## Service Endpoint Summary

### Question Service

Base path is defined in `QuestionController` as `/question`.

| Method | Endpoint | Handler |
|---|---|---|
| GET | `/question/allQuestions` | `QuestionController.getQuestions()` |
| GET | `/question/category/{category}` | `QuestionController.getQuestionsByCategory(String category)` |
| POST | `/question/add` | `QuestionController.addQuestion(QuestionRequest questionRequest)` |
| GET | `/question/getQuestionsForQuiz` | `QuestionController.getQuestionsForQuiz(String category, Integer numQuestions)` |
| POST | `/question/getQuestionsByIds` | `QuestionController.getQuestionsFromIds(List<Integer> questionIds)` |
| POST | `/question/getScore` | `QuestionController.getScore(List<QuizResponse> quizResponses)` |

### Quiz Service

Base path is defined in `QuizController` as `/quiz`.

| Method | Endpoint | Handler |
|---|---|---|
| GET | `/quiz/create` | `QuizController.createQuiz(String category, int numQ, String title)` |
| GET | `/quiz/getDetails/{id}` | `QuizController.getQuizQuestions(Integer id)` |
| POST | `/quiz/submitQuiz/{id}` | `QuizController.quizSubmit(Integer id, List<QuizResponse> quizResponses)` |

## Code References

- `ServiceRegistryApplication`: `service-registry/src/main/java/com/learning/service_registry/ServiceRegistryApplication.java`
- `ApiGatewayApplication`: `api-gateway/src/main/java/com/learning/api_gateway/ApiGatewayApplication.java`
- `QuestionController`: `question-service/src/main/java/com/learning/question/controller/QuestionController.java`
- `QuestionService`: `question-service/src/main/java/com/learning/question/service/QuestionService.java`
- `QuestionRepository`: `question-service/src/main/java/com/learning/question/respository/QuestionRepository.java`
- `QuizServiceApplication`: `quiz-service/src/main/java/com/learning/quiz/QuizServiceApplication.java`
- `QuizController`: `quiz-service/src/main/java/com/learning/quiz/controller/QuizController.java`
- `QuizService`: `quiz-service/src/main/java/com/learning/quiz/service/QuizService.java`
- `QuizRepository`: `quiz-service/src/main/java/com/learning/quiz/respository/QuizRepository.java`
- `QuestionInterface`: `quiz-service/src/main/java/com/learning/quiz/feign/QuestionInterface.java`
- Runtime setup: `docker-compose.yaml`

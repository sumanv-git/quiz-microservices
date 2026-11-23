package com.learning.quiz.bdd;

import com.learning.quiz.feign.QuestionInterface;
import com.learning.quiz.respository.QuizRepository;
import com.learning.quiz.service.QuizService;
import com.learning.quiz.service.QuestionMapper;
import com.learning.quiz.controller.QuizController;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberSpringConfig.MockConfig.class)
@ActiveProfiles("test")
public class CucumberSpringConfig {

    /**
     * Lightweight test configuration — only the beans needed for BDD tests.
     * No full Spring context, no DB, no Eureka, no Feign registration.
     *
     * @ComponentScan picks up @ScenarioScope beans (ScenarioContext, MockMvcHolder)
     * from the steps package so they can be @Autowired everywhere.
     */
    @TestConfiguration
    @ComponentScan(basePackages = "com.learning.quiz.bdd")
    static class MockConfig {

        @Bean
        public QuizRepository quizRepository() {
            return Mockito.mock(QuizRepository.class);
        }

        @Bean
        public QuestionInterface questionInterface() {
            return Mockito.mock(QuestionInterface.class);
        }

        @Bean
        public QuestionMapper questionMapper() {
            return new QuestionMapper();
        }

        @Bean
        public QuizService quizService(QuizRepository quizRepository,
                                       QuestionMapper questionMapper,
                                       QuestionInterface questionInterface) {
            return new QuizService(quizRepository, questionMapper, questionInterface);
        }

        @Bean
        public QuizController quizController(QuizService quizService) {
            return new QuizController(quizService);
        }
    }
}

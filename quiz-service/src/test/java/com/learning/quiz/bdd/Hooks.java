package com.learning.quiz.bdd;

import com.learning.quiz.bdd.steps.MockMvcHolder;
import com.learning.quiz.controller.QuizController;
import com.learning.quiz.feign.QuestionInterface;
import com.learning.quiz.respository.QuizRepository;
import com.learning.quiz.bdd.steps.ScenarioContext;
import io.cucumber.java.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Single place for all Cucumber lifecycle hooks.
 * Runs before EVERY scenario — resets all mocks and rebuilds MockMvc cleanly.
 */
public class Hooks {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionInterface questionInterface;

    @Autowired
    private QuizController quizController;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private MockMvcHolder mockMvcHolder;

    @Before
    public void beforeEachScenario() {
        // Reset all mocks so no state leaks between scenarios
        Mockito.reset(quizRepository, questionInterface);

        // Rebuild MockMvc and share it via MockMvcHolder
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(quizController).build();
        mockMvcHolder.setMockMvc(mockMvc);
    }
}

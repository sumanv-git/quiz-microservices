package com.learning.quiz.bdd.steps;

import com.learning.quiz.entity.Quiz;
import com.learning.quiz.feign.QuestionInterface;
import com.learning.quiz.respository.QuizRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CreateQuizSteps {

    @Autowired
    private QuestionInterface questionInterface;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private MockMvcHolder mockMvcHolder;  // ← shared MockMvc built in Hooks

    // ─── Given ───────────────────────────────────────────────────────────────

    @Given("the question service returns question IDs [1, 2, 3] for category {string} with 3 questions")
    public void questionServiceReturns3Ids(String category) {
        when(questionInterface.getQuestionsForQuiz(eq(category), eq(3)))
                .thenReturn(ResponseEntity.ok(List.of(1, 2, 3)));

        Quiz savedQuiz = new Quiz();
        savedQuiz.setId(10);
        savedQuiz.setCategory(category);
        savedQuiz.setNumQuestions(3);
        savedQuiz.setTitle("Java Basics");
        savedQuiz.setQuestions(List.of(1, 2, 3));
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);
    }

    @Given("the question service returns question IDs [4, 5] for category {string} with 2 questions")
    public void questionServiceReturns2Ids(String category) {
        when(questionInterface.getQuestionsForQuiz(eq(category), eq(2)))
                .thenReturn(ResponseEntity.ok(List.of(4, 5)));

        Quiz savedQuiz = new Quiz();
        savedQuiz.setId(11);
        savedQuiz.setCategory(category);
        savedQuiz.setNumQuestions(2);
        savedQuiz.setTitle("Python Intro");
        savedQuiz.setQuestions(List.of(4, 5));
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);
    }

    // ─── When ─────────────────────────────────────────────────────────────────

    @When("I create a quiz with title {string} category {string} and {int} questions")
    public void createQuiz(String title, String category, int numQ) throws Exception {
        scenarioContext.setMvcResult(
                mockMvcHolder.getMockMvc().perform(get("/quiz/create")
                        .param("category", category)
                        .param("numQ", String.valueOf(numQ))
                        .param("title", title))
                        .andReturn()
        );
    }

    // ─── Then ─────────────────────────────────────────────────────────────────

    @Then("a valid quiz ID should be returned")
    public void verifyQuizIdReturned() throws Exception {
        String body = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(body).isNotBlank();
        assertThat(Integer.parseInt(body)).isPositive();
    }
}

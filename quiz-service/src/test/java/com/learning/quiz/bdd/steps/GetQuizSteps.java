package com.learning.quiz.bdd.steps;

import com.learning.quiz.entity.Quiz;
import com.learning.quiz.feign.QuestionInterface;
import com.learning.quiz.model.QuestionResponse;
import com.learning.quiz.respository.QuizRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class GetQuizSteps {

    @Autowired
    private QuestionInterface questionInterface;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private MockMvcHolder mockMvcHolder;  // ← shared MockMvc built in Hooks

    // ─── Given ───────────────────────────────────────────────────────────────

    @Given("a quiz with ID {int} exists in the system with question IDs [1, 2]")
    public void quizExistsWithQuestionIds(int quizId) {
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setTitle("Java Basics");
        quiz.setCategory("Java");
        quiz.setNumQuestions(2);
        quiz.setQuestions(List.of(1, 2));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
    }

    @Given("the question service returns question details for IDs [1, 2]")
    public void questionServiceReturnsDetails() {
        QuestionResponse q1 = QuestionResponse.builder().id(1).questionTitle("Q1").category("Java").build();
        QuestionResponse q2 = QuestionResponse.builder().id(2).questionTitle("Q2").category("Java").build();
        when(questionInterface.getQuestionsFromIds(anyList()))
                .thenReturn(ResponseEntity.ok(List.of(q1, q2)));
    }

    @Given("no quiz exists with ID {int}")
    public void noQuizExistsWithId(int quizId) {
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());
    }

    // ─── When ─────────────────────────────────────────────────────────────────

    @When("I request quiz details for quiz ID {int}")
    public void requestQuizDetails(int quizId) throws Exception {
        try {
            scenarioContext.setMvcResult(
                    mockMvcHolder.getMockMvc().perform(get("/quiz/getDetails/" + quizId)).andReturn()
            );
        } catch (Exception e) {
            scenarioContext.setThrownException(e);
        }
    }

    // ─── Then ─────────────────────────────────────────────────────────────────

    @Then("the response should contain {int} questions")
    public void responseContainsQuestions(int expectedCount) throws Exception {
        String body = scenarioContext.getMvcResult().getResponse().getContentAsString();
        long count = body.chars().filter(c -> c == '{').count();
        assertThat(count).isEqualTo(expectedCount);
    }
}

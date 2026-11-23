package com.learning.quiz.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.quiz.model.QuizResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class SubmitQuizSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private MockMvcHolder mockMvcHolder;  // ← shared MockMvc built in Hooks

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Note: @Given("a quiz with ID {int} exists...") and @Given("no quiz exists...")
    // are defined in GetQuizSteps — Cucumber shares step definitions across all step classes.

    // ─── When ─────────────────────────────────────────────────────────────────

    @When("I submit answers for quiz ID {int}")
    public void submitAnswers(int quizId) throws Exception {
        QuizResponse response = new QuizResponse();
        response.setQuestionId(1);
        response.setRightAnswer("A language");

        try {
            scenarioContext.setMvcResult(
                    mockMvcHolder.getMockMvc().perform(post("/quiz/submitQuiz/" + quizId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(List.of(response))))
                            .andReturn()
            );
        } catch (Exception e) {
            scenarioContext.setThrownException(e);
        }
    }

    // ─── Then ─────────────────────────────────────────────────────────────────

    @Then("the score returned should be {int}")
    public void scoreReturnedShouldBe(int expectedScore) throws Exception {
        String body = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(Integer.parseInt(body)).isEqualTo(expectedScore);
    }
}

package com.learning.quiz.bdd.steps;

import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared @Then steps used across all feature files.
 * Reads from ScenarioContext so result is always from the current scenario's @When step.
 */
public class SharedSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Then("the response status should be {int}")
    public void verifyStatus(int expectedStatus) {
        assertThat(scenarioContext.getMvcResult().getResponse().getStatus())
                .isEqualTo(expectedStatus);
    }

    @Then("a {string} runtime exception should be thrown")
    public void runtimeExceptionShouldBeThrown(String expectedMessage) {
        Exception thrown = scenarioContext.getThrownException();
        if (thrown != null) {
            assertThat(thrown.getMessage()).contains(expectedMessage);
        } else {
            Exception resolvedException = scenarioContext.getMvcResult().getResolvedException();
            assertThat(resolvedException).isNotNull();
            assertThat(resolvedException.getMessage()).contains(expectedMessage);
        }
    }
}

package com.learning.quiz.bdd.steps;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Shared scenario-scoped state — a fresh instance is created per Cucumber scenario.
 * All step classes inject this to share MvcResult and exceptions across steps.
 */
@Component
@ScenarioScope
public class ScenarioContext {
    private MvcResult mvcResult;
    private Exception thrownException;

    public MvcResult getMvcResult() { return mvcResult; }
    public void setMvcResult(MvcResult mvcResult) { this.mvcResult = mvcResult; }

    public Exception getThrownException() { return thrownException; }
    public void setThrownException(Exception e) { this.thrownException = e; }
}

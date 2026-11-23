package com.learning.quiz.bdd.steps;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Scenario-scoped holder for MockMvc.
 * Built once per scenario in Hooks and injected into all step classes.
 */
@Component
@ScenarioScope
public class MockMvcHolder {
    private MockMvc mockMvc;

    public MockMvc getMockMvc() { return mockMvc; }
    public void setMockMvc(MockMvc mockMvc) { this.mockMvc = mockMvc; }
}

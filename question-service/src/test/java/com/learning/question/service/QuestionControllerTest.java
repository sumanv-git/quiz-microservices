package com.learning.question.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.question.controller.QuestionController;
import com.learning.question.exception.QuestionServiceException;
import com.learning.question.exception.RestResponseEntityExceptionHandler;
import com.learning.question.model.QuestionRequest;
import com.learning.question.model.QuestionResponse;
import com.learning.question.model.QuizResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private QuestionResponse questionResponse;

    @BeforeEach
    void setUp() {
        // Inject a MockEnvironment so environment.getProperty() works in getScore
        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty("local.server.port", "8080");
        questionController = new QuestionController();
        // Use reflection-friendly field injection via MockMvcBuilders
        mockMvc = MockMvcBuilders
                .standaloneSetup(questionController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();

        // Re-inject mocks after manual construction
        org.springframework.test.util.ReflectionTestUtils.setField(questionController, "questionService", questionService);
        org.springframework.test.util.ReflectionTestUtils.setField(questionController, "environment", mockEnvironment);

        questionResponse = QuestionResponse.builder()
                .id(1)
                .questionTitle("What is Java?")
                .option1("A language")
                .option2("A framework")
                .option3("A database")
                .option4("An OS")
                .difficultylevel("EASY")
                .category("Java")
                .build();
    }

    // ─── GET /question/allQuestions ───────────────────────────────────────────

    @Test
    void getQuestions_returns200WithList_whenQuestionsExist() throws Exception {
        when(questionService.getAllQuestions()).thenReturn(List.of(questionResponse));

        mockMvc.perform(get("/question/allQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].questionTitle", is("What is Java?")))
                .andExpect(jsonPath("$[0].category", is("Java")));
    }

    @Test
    void getQuestions_returns404_whenNoQuestionsExist() throws Exception {
        when(questionService.getAllQuestions())
                .thenThrow(new QuestionServiceException("No questions found", "QUSTION_NOT_FOUND"));

        mockMvc.perform(get("/question/allQuestions"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("QUSTION_NOT_FOUND")))
                .andExpect(jsonPath("$.errorMessage", is("No questions found")));
    }

    // ─── GET /question/category/{category} ───────────────────────────────────

    @Test
    void getQuestionsByCategory_returns200WithList_whenCategoryExists() throws Exception {
        when(questionService.getQuestionsByCategory("Java")).thenReturn(List.of(questionResponse));

        mockMvc.perform(get("/question/category/Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Java")));
    }

    @Test
    void getQuestionsByCategory_returns404_whenCategoryNotFound() throws Exception {
        when(questionService.getQuestionsByCategory("Unknown"))
                .thenThrow(new QuestionServiceException("No questions found for category", "QUSTION_NOT_FOUND_FOR_CATEGORY"));

        mockMvc.perform(get("/question/category/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("QUSTION_NOT_FOUND_FOR_CATEGORY")));
    }

    // ─── POST /question/add ───────────────────────────────────────────────────

    @Test
    void addQuestion_returns200WithId_whenQuestionIsAdded() throws Exception {
        QuestionRequest request = QuestionRequest.builder()
                .questionTitle("What is Java?")
                .option1("A language")
                .option2("A framework")
                .option3("A database")
                .option4("An OS")
                .rightAnswer("A language")
                .difficultylevel("EASY")
                .category("Java")
                .build();

        when(questionService.addQuestion(any(QuestionRequest.class))).thenReturn(42);

        mockMvc.perform(post("/question/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(42)));
    }

    // ─── GET /question/getQuestionsForQuiz ────────────────────────────────────

    @Test
    void getQuestionsForQuiz_returns200WithIds_whenQuestionsExist() throws Exception {
        when(questionService.getQuestionsForQuiz("Java", 2)).thenReturn(List.of(1, 2));

        mockMvc.perform(get("/question/getQuestionsForQuiz")
                        .param("category", "Java")
                        .param("numQuestions", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is(1)))
                .andExpect(jsonPath("$[1]", is(2)));
    }

    @Test
    void getQuestionsForQuiz_returns404_whenNoQuestionsForCategory() throws Exception {
        when(questionService.getQuestionsForQuiz(anyString(), anyInt()))
                .thenThrow(new QuestionServiceException("No questions found for category", "QUSTION_NOT_FOUND_FOR_CATEGORY"));

        mockMvc.perform(get("/question/getQuestionsForQuiz")
                        .param("category", "Unknown")
                        .param("numQuestions", "5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("QUSTION_NOT_FOUND_FOR_CATEGORY")));
    }

    // ─── POST /question/getQuestionsByIds ─────────────────────────────────────

    @Test
    void getQuestionsFromIds_returns200WithResponses_forGivenIds() throws Exception {
        when(questionService.getQuestionsByIds(List.of(1))).thenReturn(List.of(questionResponse));

        mockMvc.perform(post("/question/getQuestionsByIds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getQuestionsFromIds_returns200WithEmptyList_whenNoIdsMatch() throws Exception {
        when(questionService.getQuestionsByIds(List.of(99))).thenReturn(List.of());

        mockMvc.perform(post("/question/getQuestionsByIds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(99))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── POST /question/getScore ──────────────────────────────────────────────

    @Test
    void getScore_returns200WithCorrectScore() throws Exception {
        QuizResponse quizResponse = new QuizResponse();
        quizResponse.setQuestionId(1);
        quizResponse.setRightAnswer("A language");

        when(questionService.getScore(any())).thenReturn(1);

        mockMvc.perform(post("/question/getScore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(quizResponse))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    void getScore_returns200WithZero_whenAllAnswersWrong() throws Exception {
        QuizResponse quizResponse = new QuizResponse();
        quizResponse.setQuestionId(1);
        quizResponse.setRightAnswer("Wrong answer");

        when(questionService.getScore(any())).thenReturn(0);

        mockMvc.perform(post("/question/getScore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(quizResponse))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0)));
    }
}

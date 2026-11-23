package com.learning.question.service;

import com.learning.question.entity.Question;
import com.learning.question.exception.QuestionServiceException;
import com.learning.question.model.QuestionRequest;
import com.learning.question.model.QuestionResponse;
import com.learning.question.model.QuizResponse;
import com.learning.question.respository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionService questionService;

    private Question question;
    private QuestionResponse questionResponse;

    @BeforeEach
    void setUp() {
        question = Question.builder()
                .id(1)
                .questionTitle("What is Java?")
                .option1("A language")
                .option2("A framework")
                .option3("A database")
                .option4("An OS")
                .rightAnswer("A language")
                .difficultylevel("EASY")
                .category("Java")
                .build();

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

    // ─── getAllQuestions ──────────────────────────────────────────────────────

    @Test
    void getAllQuestions_returnsListOfResponses_whenQuestionsExist() {
        when(questionRepository.findAll()).thenReturn(List.of(question));
        when(questionMapper.toDto(question)).thenReturn(questionResponse);

        List<QuestionResponse> result = questionService.getAllQuestions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuestionTitle()).isEqualTo("What is Java?");
        verify(questionRepository).findAll();
    }

    @Test
    void getAllQuestions_throwsQuestionServiceException_whenNoQuestionsExist() {
        when(questionRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> questionService.getAllQuestions())
                .isInstanceOf(QuestionServiceException.class)
                .hasMessage("No questions found")
                .extracting("errorCode")
                .isEqualTo("QUSTION_NOT_FOUND");
    }

    // ─── getQuestionsByCategory ───────────────────────────────────────────────

    @Test
    void getQuestionsByCategory_returnsListOfResponses_whenCategoryHasQuestions() {
        when(questionRepository.findByCategory("Java")).thenReturn(List.of(question));
        when(questionMapper.toDto(question)).thenReturn(questionResponse);

        List<QuestionResponse> result = questionService.getQuestionsByCategory("Java");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Java");
    }

    @Test
    void getQuestionsByCategory_throwsQuestionServiceException_whenCategoryHasNoQuestions() {
        when(questionRepository.findByCategory("Unknown")).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> questionService.getQuestionsByCategory("Unknown"))
                .isInstanceOf(QuestionServiceException.class)
                .hasMessage("No questions found for category")
                .extracting("errorCode")
                .isEqualTo("QUSTION_NOT_FOUND_FOR_CATEGORY");
    }

    // ─── addQuestion ──────────────────────────────────────────────────────────

    @Test
    void addQuestion_savesEntityAndReturnsId() {
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

        // Simulate the repository setting the generated id on save
        doAnswer(invocation -> {
            Question q = invocation.getArgument(0);
            q.setId(42);
            return null;
        }).when(questionRepository).save(any(Question.class));

        Integer id = questionService.addQuestion(request);

        assertThat(id).isEqualTo(42);
        verify(questionRepository).save(any(Question.class));
    }

    // ─── getQuestionsForQuiz ──────────────────────────────────────────────────

    @Test
    void getQuestionsForQuiz_returnsListOfIds_whenQuestionsExist() {
        when(questionRepository.findRandonQuestionsByCategory("Java", 2))
                .thenReturn(List.of(question));

        List<Integer> ids = questionService.getQuestionsForQuiz("Java", 2);

        assertThat(ids).containsExactly(1);
    }

    @Test
    void getQuestionsForQuiz_throwsQuestionServiceException_whenNoQuestionsFound() {
        when(questionRepository.findRandonQuestionsByCategory("Unknown", 5))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> questionService.getQuestionsForQuiz("Unknown", 5))
                .isInstanceOf(QuestionServiceException.class)
                .hasMessage("No questions found for category")
                .extracting("errorCode")
                .isEqualTo("QUSTION_NOT_FOUND_FOR_CATEGORY");
    }

    // ─── getQuestionsByIds ────────────────────────────────────────────────────

    @Test
    void getQuestionsByIds_returnsListOfResponses_forGivenIds() {
        when(questionRepository.findAllById(List.of(1))).thenReturn(List.of(question));
        when(questionMapper.toDto(question)).thenReturn(questionResponse);

        List<QuestionResponse> result = questionService.getQuestionsByIds(List.of(1));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
    }

    // ─── getScore ─────────────────────────────────────────────────────────────

    @Test
    void getScore_returnsCorrectScore_whenAnswersMatch() {
        QuizResponse response = new QuizResponse();
        response.setQuestionId(1);
        response.setRightAnswer("A language");

        when(questionRepository.findById(1)).thenReturn(Optional.of(question));

        Integer score = questionService.getScore(List.of(response));

        assertThat(score).isEqualTo(1);
    }

    @Test
    void getScore_returnsZero_whenAnswersDoNotMatch() {
        QuizResponse response = new QuizResponse();
        response.setQuestionId(1);
        response.setRightAnswer("Wrong answer");

        when(questionRepository.findById(1)).thenReturn(Optional.of(question));

        Integer score = questionService.getScore(List.of(response));

        assertThat(score).isZero();
    }

    @Test
    void getScore_returnsZero_whenQuestionIdDoesNotExist() {
        QuizResponse response = new QuizResponse();
        response.setQuestionId(99);
        response.setRightAnswer("A language");

        when(questionRepository.findById(99)).thenReturn(Optional.empty());

        Integer score = questionService.getScore(List.of(response));

        assertThat(score).isZero();
    }
}

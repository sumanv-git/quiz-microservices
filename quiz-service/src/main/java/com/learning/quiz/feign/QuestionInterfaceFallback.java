package com.learning.quiz.feign;

import com.learning.quiz.model.QuestionResponse;
import com.learning.quiz.model.QuizResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class QuestionInterfaceFallback implements QuestionInterface {

    @Override
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String category, Integer numQuestions) {
        log.warn("Fallback: question-service is unavailable. getQuestionsForQuiz called with category={}, numQ={}", category, numQuestions);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Collections.emptyList());
    }

    @Override
    public ResponseEntity<List<QuestionResponse>> getQuestionsFromIds(List<Integer> questionIds) {
        log.warn("Fallback: question-service is unavailable. getQuestionsFromIds called with ids={}", questionIds);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Collections.emptyList());
    }

    @Override
    public ResponseEntity<Integer> getScore(List<QuizResponse> quizResponses) {
        log.warn("Fallback: question-service is unavailable. getScore called.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(0);
    }
}

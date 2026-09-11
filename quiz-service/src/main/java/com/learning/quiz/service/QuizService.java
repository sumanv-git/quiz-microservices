package com.learning.quiz.service;


import com.learning.quiz.entity.Quiz;
import com.learning.quiz.feign.QuestionInterface;
import com.learning.quiz.model.QuestionResponse;
import com.learning.quiz.model.QuizResponse;
import com.learning.quiz.respository.QuizRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionMapper questionMapper;

     private final QuestionInterface questionInterface;

    public QuizService(QuizRepository quizRepository, QuestionMapper questionMapper, QuestionInterface questionInterface) {
        this.quizRepository = quizRepository;
        this.questionMapper = questionMapper;
        this.questionInterface = questionInterface;
    }

    public Integer createQuiz(String category, int numQ, String title) {
        log.info("Creating quiz with category: {}, title: {}, numQuestions: {}", category, title, numQ);
        List<Integer> questionIds = questionInterface.getQuestionsForQuiz(category, numQ).getBody();
        Quiz quiz = new Quiz();
        quiz.setCategory(category);
        quiz.setNumQuestions(numQ);
        quiz.setTitle(title);
        quiz.setQuestions(questionIds);
        return quizRepository.save(quiz).getId();
    }




    public Integer submitQuiz(Integer id, List<QuizResponse> quizResponses) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
//        List<Question> quizQuestions = quiz.getQuestions();
//
        int correctAnswers = 0;
//        for (QuizResponse quizResponse : quizResponses) {
//            Question question = quizQuestions.stream()
//                    .filter(q -> q.getId().equals(quizResponse.getQuestionId()))
//                    .findFirst()
//                    .orElse(null);
//            if (question != null && question.getRightAnswer().equals(quizResponse.getRightAnswer()))
//                correctAnswers++;
//        }
        return correctAnswers;

    }

    public List<QuestionResponse> getAllQuestions(Integer id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        List<Integer> questionIds = quiz.getQuestions();
        return questionInterface.getQuestionsFromIds(questionIds).getBody();
    }
}

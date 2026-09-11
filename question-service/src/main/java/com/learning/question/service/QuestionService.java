package com.learning.question.service;

import com.learning.question.entity.Question;
import com.learning.question.model.QuestionRequest;
import com.learning.question.model.QuestionResponse;
import com.learning.question.model.QuizResponse;
import com.learning.question.respository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuestionService {
    private final QuestionRepository questionRepository;
    private  final QuestionMapper questionMapper;

    public QuestionService(QuestionRepository questionRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    public List<QuestionResponse> getAllQuestions() {
       List<Question> questions = questionRepository.findAll();

        return questions.stream()
                                        .map(questionMapper::toDto)
                                        .toList();
    }

    public List<QuestionResponse> getQuestionsByCategory(String category) {
        List<Question> questions = questionRepository.findByCategory(category);
        return questions.stream()
                .map(questionMapper::toDto)
                .toList();
    }

    public Integer addQuestion(QuestionRequest questionRequest) {
        Question question = QuestionMapper.toEntity(questionRequest);
        questionRepository.save(question);
        return question.getId();
    }

    public List<Integer> getQuestionsForQuiz(String category, Integer numQuestions) {
         List<Question> questions = questionRepository.findRandonQuestionsByCategory(category, numQuestions);
         if(questions.isEmpty())
             throw new RuntimeException("No questions found for category: " + category);

         return questions.stream().mapToInt(Question::getId).boxed().toList();
    }

    public List<QuestionResponse> getQuestionsByIds(List<Integer> questionIds) {
        List<Question> questions = questionRepository.findAllById(questionIds);
        return questions.stream()
                .map(questionMapper::toDto)
                .toList();
    }

    public Integer getScore(List<QuizResponse> quizResponses) {
        int score = 0;
         for(QuizResponse response : quizResponses) {
             Question question = questionRepository.findById(response.getQuestionId())
                     .orElse(null);
             if(question != null && question.getRightAnswer().equals(response.getRightAnswer()))
                 score++;
         }
        return score;
    }
}

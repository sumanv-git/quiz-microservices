package com.learning.quiz.controller;

import com.learning.quiz.model.QuestionResponse;
import com.learning.quiz.model.QuizResponse;
import com.learning.quiz.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("create")
    public ResponseEntity<Integer> createQuiz(
            @RequestParam String category,
            @RequestParam int numQ,
            @RequestParam String title
    ){
        Integer quizId = quizService.createQuiz(category, numQ, title);
        return new ResponseEntity<>(quizId,HttpStatus.CREATED); //constructor based
        //return ResponseEntity.status(HttpStatus.CREATED).body(quizId); //static facotry method

    }

    @GetMapping("getDetails/{id}")
    public ResponseEntity<List<QuestionResponse>> getQuizQuestions(@PathVariable Integer id){
        List<QuestionResponse> questions = quizService.getAllQuestions(id);
        return new ResponseEntity<>(questions, HttpStatus.OK);
        //return ResponseEntity.ok(questions);
    }

    @PostMapping("submitQuiz/{id}")
    public ResponseEntity<Integer> quizSubmit(@PathVariable Integer id, @RequestBody List<QuizResponse> quizResponses){

        Integer score = quizService.submitQuiz(id, quizResponses);
        return new ResponseEntity<>(score, HttpStatus.OK);
    }

}

package com.learning.question.controller;


import com.learning.question.model.QuestionRequest;
import com.learning.question.model.QuestionResponse;
import com.learning.question.model.QuizResponse;
import com.learning.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
public class QuestionController {

    @Autowired
    QuestionService questionService;
    @Autowired
    private Environment environment;

    @GetMapping("allQuestions")
    public  ResponseEntity<List<QuestionResponse>> getQuestions(){

        List<QuestionResponse> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
        //return new ResponseEntity<>(questions, HttpStatus.OK);
    }


    @GetMapping("category/{category}")
    //@GetMapping("category/{cat}")
    //public ResponseEntity<List<QuestionDto>> getQuestionsByCategory(@PathVariable("cat") String category){
    public ResponseEntity<List<QuestionResponse>> getQuestionsByCategory(@PathVariable String category){
        var questions = questionService.getQuestionsByCategory(category);
        return ResponseEntity.ok(questions);
        //return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("add")
    public ResponseEntity<Integer> addQuestion(@RequestBody QuestionRequest questionRequest){
         Integer id = questionService.addQuestion(questionRequest);
         return ResponseEntity.ok(id);
    }

    @GetMapping("getQuestionsForQuiz")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(@RequestParam String category, @RequestParam Integer numQuestions){

        List<Integer> questionIds = questionService.getQuestionsForQuiz(category, numQuestions);
        return new ResponseEntity<>(questionIds, HttpStatus.OK);

    }

    @PostMapping("getQuestionsByIds")
    public ResponseEntity<List<QuestionResponse>> getQuestionsFromIds(@RequestBody List<Integer> questionIds){
        List<QuestionResponse> questionResponses = questionService.getQuestionsByIds(questionIds);
        return ResponseEntity.ok(questionResponses);
    }

    @PostMapping("getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<QuizResponse> quizResponses){
        Integer score = questionService.getScore(quizResponses);
        System.out.println(environment.getProperty("local.server.port"));
        return ResponseEntity.ok(score);
    }

}

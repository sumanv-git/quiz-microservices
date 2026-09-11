package com.learning.quiz.model;

import lombok.Data;

@Data
public class QuizResponse {
    private Integer questionId;
    private String rightAnswer;
}

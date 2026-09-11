package com.learning.quiz.model;

import lombok.Data;

@Data
public class QuestionRequest {
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String difficultylevel;
    private String rightAnswer;
    private String category;
}

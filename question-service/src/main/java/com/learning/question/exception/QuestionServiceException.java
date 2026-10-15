package com.learning.question.exception;

import lombok.Data;

@Data
public class QuestionServiceException extends RuntimeException {
    String errorCode;
    public QuestionServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

package com.learning.quiz.exception;

import lombok.Data;

@Data
public class CustomException extends RuntimeException  {
    private String errorCode;
    private int statusCode;
    public CustomException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}

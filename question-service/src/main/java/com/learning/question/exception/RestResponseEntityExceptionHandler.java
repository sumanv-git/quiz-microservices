package com.learning.question.exception;


import com.learning.question.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(QuestionServiceException.class)
    public ResponseEntity<ErrorResponse> handleQuestionServiceException(QuestionServiceException exception){
        log.error("QuestionServiceException: {}", exception.getErrorCode(), exception);
        return new ResponseEntity<>(ErrorResponse.builder()
                        .errorMessage(exception.getMessage())
                        .errorCode(exception.getErrorCode())
                .build(), HttpStatus.NOT_FOUND);
    }

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
//                                                                   HttpHeaders headers, HttpStatus status, WebRequest request){
//        log.error("Validation failed: {}", exception.getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorMessage("Request validation failed: " + exception.getBindingResult().getFieldError().getDefaultMessage())
//                        .errorCode("VALIDATION_FAILED")
//                .build();
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception){
        log.error("Illegal argument: {}", exception.getMessage());
        return new ResponseEntity<>(ErrorResponse.builder()
                        .errorMessage(exception.getMessage())
                        .errorCode("INVALID_ARGUMENT")
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DataAccessException exception){
        log.error("Database error: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(ErrorResponse.builder()
                        .errorMessage("Database operation failed")
                        .errorCode("DATABASE_ERROR")
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException exception){
        log.error("Null pointer exception: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(ErrorResponse.builder()
                        .errorMessage("An unexpected error occurred")
                        .errorCode("NULL_POINTER_ERROR")
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception){
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(ErrorResponse.builder()
                        .errorMessage("An unexpected error occurred: " + exception.getMessage())
                        .errorCode("INTERNAL_SERVER_ERROR")
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.learning.quiz.exception;



import com.learning.quiz.external.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomServiceException(CustomException exception){
        return new ResponseEntity<>(ErrorResponse.builder()
                        .errorMessage(exception.getMessage())
                        .errorCode(exception.getErrorCode())
                .build(), HttpStatus.valueOf(exception.getStatusCode()));
    }
}

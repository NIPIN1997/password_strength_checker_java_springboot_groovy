package com.projectsbynipin.passwordruleengine.exception;

import com.projectsbynipin.passwordruleengine.dto.ApiResponse;
import com.projectsbynipin.passwordruleengine.utils.ApiResponseCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FailedToCheckPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleFailedToCheckPasswordException(FailedToCheckPasswordException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

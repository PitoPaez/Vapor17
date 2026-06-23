package com.plataforma.Vapor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class Exceptions {

   @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericError(Exception e) {
        ApiError error = new ApiError(500, "Data integrity error for the entered data", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
package com.sioma.spotsapi.web.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex){

        Map<String,String> error = new HashMap<>();

        error.put("error","BUSINESS_ERROR");
        error.put("message",ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}
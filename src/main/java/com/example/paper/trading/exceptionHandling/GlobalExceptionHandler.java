package com.example.paper.trading.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException exp) {
        Map<String, String> response = new HashMap<>();
        response.put("error", exp.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exp
    ) {
        Map<String, String> response = new HashMap<>();
        var error = exp.getBindingResult().getAllErrors().getFirst();
        String errorField = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        response.put("error", errorField + ": " + errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

package com.brickbybrick.recipes.admin;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors); // status 400
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Map<String, String>> handleUnknownField(UnrecognizedPropertyException ex, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Unknown field: " + ex.getPropertyName());
        return ResponseEntity.badRequest().body(error);
    }
}
package com.foodorder.foodapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;


import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            404,
            "Not Found"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
       Map<String, Object> response = new HashMap<>();
       response.put("status", HttpStatus.BAD_REQUEST.value());
       response.put("error", "Validate Failed");

       Map<String, String> fieldErrors = new HashMap<>();
       ex.getBindingResult().getFieldErrors().forEach(error -> {
           fieldErrors.put(error.getField(), error.getDefaultMessage());
       });
       response.put("fieldErrors", fieldErrors);
       return response;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map <String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");

        Map<String, String> constraintViolations = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            constraintViolations.put(fieldName, violation.getMessage());
        });
        response.put("constraintViolations", constraintViolations);

        return response;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid request body");

        String message = ex.getMostSpecificCause().getMessage();
        if (message.contains("java.lang.Long")) {
            message = "departmentId must be a valid number";
        }

        response.put("message", message);
        return response;
    }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
      Map<String, Object> response = new HashMap<>();
      response.put("status", HttpStatus.BAD_REQUEST.value());
      response.put("error", "Invalid path or parameter type");

      Map<String, String> errors = new HashMap<>();

      String field = ex.getName();
      String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
      String message = String.format(
          "Invalid value for parameter '%s': expected type %s",
          field,
          expectedType
      );

      errors.put(field, message);
      response.put("errors", errors);

      return response;
  }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            500,
            "Internal Server Error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

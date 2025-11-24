package com.foodorder.foodapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @Autowired
  private MessageSource messageSource;
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    String localizedMessage = messageSource.getMessage(
        ex.getMessage(),
        null,
        LocaleContextHolder.getLocale());

    ErrorResponse errorResponse = new ErrorResponse(
        localizedMessage,
        404,
        "RESOURCE_NOT_FOUND");
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    });

    ErrorResponse errorResponse = new ErrorResponse(
        "Request validation failed",
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        fieldErrors);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
    Map<String, String> constraintViolations = new HashMap<>();
    ex.getConstraintViolations().forEach(violation -> {
      String propertyPath = violation.getPropertyPath().toString();
      String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
      constraintViolations.put(fieldName, violation.getMessage());
    });

    ErrorResponse errorResponse = new ErrorResponse(
        "Constraint validation failed",
        HttpStatus.BAD_REQUEST.value(),
        "CONSTRAINT_VIOLATION",
        constraintViolations);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    String message;
    String rootCause = ex.getMostSpecificCause().getMessage();

    if (rootCause != null && rootCause.contains("java.lang.Long")) {
      message = "Invalid number format. Please provide a valid number.";
    } else {
      message = "Invalid request format. Please check your request body and try again.";
    }

    ErrorResponse errorResponse = new ErrorResponse(
        message,
        HttpStatus.BAD_REQUEST.value(),
        "INVALID_REQUEST_BODY");

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    Map<String, String> errors = new HashMap<>();

    String field = ex.getName();
    Class<?> requiredType = ex.getRequiredType();
    String expectedType = requiredType != null ? requiredType.getSimpleName() : "unknown";
    String message = String.format(
        "Invalid value for parameter '%s': expected type %s",
        field,
        expectedType);

    errors.put(field, message);

    ErrorResponse errorResponse = new ErrorResponse(
        "Invalid path or parameter type",
        HttpStatus.BAD_REQUEST.value(),
        "INVALID_PARAMETER_TYPE",
        errors);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
    String localizedMessage = messageSource.getMessage(
        ex.getMessage(),
        null,
        LocaleContextHolder.getLocale());

    ErrorResponse errorResponse = new ErrorResponse(
        localizedMessage,
        400,
        "BAD_REQUEST");

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
    String localizedMessage = messageSource.getMessage(
        ex.getMessage(),
        null,
        LocaleContextHolder.getLocale());

    ErrorResponse errorResponse = new ErrorResponse(
        localizedMessage,
        401,
        "UNAUTHORIZED");

    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    logger.error("Unhandled exception occurred: ", ex);
    ErrorResponse errorResponse = new ErrorResponse(
        "An unexpected error occurred. Please try again later.",
        500,
        "INTERNAL_SERVER_ERROR");
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

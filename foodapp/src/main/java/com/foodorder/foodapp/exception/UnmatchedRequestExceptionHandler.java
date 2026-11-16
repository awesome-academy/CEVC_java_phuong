package com.foodorder.foodapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Handles requests that match no controller method: unknown URL, wrong HTTP method, wrong content type.
 * These errors happen before a controller is selected, so package-scoped advices
 * (GlobalExceptionHandler / AdminExceptionHandler) never see them.
 * API requests get JSON, other requests get the admin error page.
 */
@ControllerAdvice
public class UnmatchedRequestExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public Object handleNoResourceFound(HttpServletRequest request) {
    return respond(request, HttpStatus.NOT_FOUND, "Not found", AdminExceptionHandler.notFoundPage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public Object handleMethodNotSupported(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
    return respond(request, HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(),
        AdminExceptionHandler.errorPage(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", ex.getMessage()));
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public Object handleMediaTypeNotSupported(HttpServletRequest request, HttpMediaTypeNotSupportedException ex) {
    return respond(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(),
        AdminExceptionHandler.errorPage(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", ex.getMessage()));
  }

  private Object respond(HttpServletRequest request, HttpStatus status, String message, ModelAndView page) {
    if (request.getRequestURI().startsWith("/api/")) {
      ErrorResponse errorResponse = new ErrorResponse(message, status.value(), status.name());
      return new ResponseEntity<>(errorResponse, status);
    }

    return page;
  }
}

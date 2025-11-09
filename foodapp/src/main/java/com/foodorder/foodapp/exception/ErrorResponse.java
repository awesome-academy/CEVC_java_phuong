package com.foodorder.foodapp.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class ErrorResponse {
  private String message;
  private int status;
  private String error;
  private Map<String, String> errors; // For field-specific errors

  // Constructor for simple errors (without field errors)
  public ErrorResponse(String message, int status, String error) {
    this.message = message;
    this.status = status;
    this.error = error;
  }

  // Constructor for validation errors (with field errors)
  public ErrorResponse(String message, int status, String error, Map<String, String> errors) {
    this.message = message;
    this.status = status;
    this.error = error;
    this.errors = errors;
  }
}

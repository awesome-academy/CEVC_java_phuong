package com.foodorder.foodapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API response wrapper")
public class ApiResponseDTO<T> {

  @Schema(description = "Response data")
  private T data;

  @Schema(description = "Response error message")
  private String error;

  public static <T> ApiResponseDTO<T> success(T data) {
    return new ApiResponseDTO<>(data, null);
  }

  public static <T> ApiResponseDTO<T> error(String errorMessage) {
    return new ApiResponseDTO<>(null, errorMessage);
  }

  public static <T> ResponseEntity<ApiResponseDTO<T>> ok(T data) {
    return new ResponseEntity<>(
        success(data),
        HttpStatus.OK);
  }

  public static <T> ResponseEntity<ApiResponseDTO<T>> created(T data, String uriPath) {
    return ResponseEntity.created(URI.create(uriPath))
        .body(success(data));
  }

  public static <T> ResponseEntity<ApiResponseDTO<T>> badRequest(String errorMessage) {
    return new ResponseEntity<>(
        error(errorMessage),
        HttpStatus.BAD_REQUEST);
  }

  public static <T> ResponseEntity<ApiResponseDTO<T>> notFound(String errorMessage) {
    return new ResponseEntity<>(
        error(errorMessage),
        HttpStatus.NOT_FOUND);
  }

  public static <T> ResponseEntity<ApiResponseDTO<T>> internalError(String errorMessage) {
    return new ResponseEntity<>(
        error(errorMessage),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static <T> ResponseEntity<Void> noContent() {
    return ResponseEntity.noContent().build();
  }
}

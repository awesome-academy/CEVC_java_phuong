package com.foodorder.foodapp.controller.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.suggestion.DetailSuggestionDTO;
import com.foodorder.foodapp.dto.suggestion.CreateSuggestionDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.service.ClientSuggestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/product-suggestions")
@AllArgsConstructor
@Validated
@Tag(name = "product-suggestions", description = "Api Client for product suggestions")
@SecurityRequirement(name = "bearerAuth")
public class SuggestionsApiController {
  private final ClientSuggestionService clientSuggestionService;

  @GetMapping
  @Operation(summary = "List suggestion for user", description = "Get list of product suggestions for user")
  public ResponseEntity<?> listSuggestions(
      @AuthenticationPrincipal User currentUser) {
    List<DetailSuggestionDTO> response = clientSuggestionService.getSuggestions(currentUser);

    return ApiResponseDTO.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Detail suggestion", description = "Get detail of a product suggestion")
  public ResponseEntity<?> detailSuggestion(
      @PathVariable Long id,
      @AuthenticationPrincipal User currentUser) {
    DetailSuggestionDTO response = clientSuggestionService.getSuggestion(currentUser, id);

    return ApiResponseDTO.ok(response);
  }

  @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Create product suggestion", description = "Create a new product suggestion")
  public ResponseEntity<?> createSuggestion(
      @PathVariable Long productId,
      @Valid CreateSuggestionDTO createSuggestionDTO,
      @AuthenticationPrincipal User currentUser) {
    DetailSuggestionDTO response = clientSuggestionService.createSuggestion(currentUser,
        createSuggestionDTO);

    return ApiResponseDTO.created(response, "/api/product-suggestions/" + response.getId());
  }

  @PutMapping(path = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Update product suggestion", description = "Update an existing product suggestion")
  public ResponseEntity<?> updateSuggestion(
      @PathVariable Long id,
      @Valid CreateSuggestionDTO createSuggestionDTO,
      @AuthenticationPrincipal User currentUser) {
    DetailSuggestionDTO response = clientSuggestionService.updateSuggestion(currentUser,
        id, createSuggestionDTO);

    return ApiResponseDTO.ok(response);
  }

  @DeleteMapping("/{id}/delete")
  @Operation(summary = "Delete suggestion", description = "Delete a product suggestion")
  public ResponseEntity<?> deleteSuggestion(
      @PathVariable Long id,
      @AuthenticationPrincipal User currentUser) {

    clientSuggestionService.deleteSuggestion(currentUser, id);

    return ApiResponseDTO.noContent();
  }
}

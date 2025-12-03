package com.foodorder.foodapp.controller.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.evaluate.ClientDetailEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.CreateEvaluateDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.service.ClientEvaluateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/product-evaluates")
@AllArgsConstructor
@Validated
@Tag(name = "product-evaluates", description = "Api Client for product evaluates")
@SecurityRequirement(name = "bearerAuth")
public class EvaluationsApiController {
  private final ClientEvaluateService clientEvaluateService;

  @GetMapping("/{productId}")
  @Operation(summary = "Detail evaluate", description = "Get detail of a product evaluate")
  public ResponseEntity<?> detailEvaluate(
      @PathVariable Long productId,
      @AuthenticationPrincipal User currentUser) {
    ClientDetailEvaluateDTO response = clientEvaluateService.getEvaluate(currentUser, productId);

    return ApiResponseDTO.ok(response);
  }

  @PostMapping(path = "/{productId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Create & Update product evaluate", description = "Create or update a product evaluate")
  public ResponseEntity<?> createOrUpdateEvaluation(
      @PathVariable Long productId,
      @Valid CreateEvaluateDTO createEvaluateDTO,
      @AuthenticationPrincipal User currentUser) {
    ClientDetailEvaluateDTO response = clientEvaluateService.createEvaluate(currentUser, productId,
        createEvaluateDTO);

    return ApiResponseDTO.created(response, "/api/product-evaluates/" + productId);
  }

  @DeleteMapping("/{productId}/delete")
  @Operation(summary = "Delete evaluate", description = "Delete a product evaluate")
  public ResponseEntity<?> deleteEvaluate(
      @PathVariable Long productId,
      @AuthenticationPrincipal User currentUser) {

    clientEvaluateService.deleteEvaluate(currentUser, productId);

    return ApiResponseDTO.noContent();
  }
}

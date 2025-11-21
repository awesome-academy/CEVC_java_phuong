package com.foodorder.foodapp.controller.api.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.category.ListCategoryDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
@Tag(name = "categories", description = "Api Client for Categories")
public class CategoriesApiController {

  private final CategoryService categoryService;

  @GetMapping
  @Operation(summary = "List categories", description = "Get list of categories for client")
  public ResponseEntity<?> indexCategory() {
    List<ListCategoryDTO> categories = categoryService.getCategorySelectOptions();

    return ApiResponseDTO.ok(categories);
  }
}

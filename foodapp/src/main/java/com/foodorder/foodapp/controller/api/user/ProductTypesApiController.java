package com.foodorder.foodapp.controller.api.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.product_type.ProductTypeDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.service.ClientProductTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/product-types")
@AllArgsConstructor
@Tag(name = "product-types", description = "Api Client for Product Types")
public class ProductTypesApiController {

  private final ClientProductTypeService productTypeService;

  @GetMapping
  @Operation(summary = "List product types", description = "Get list of product types for client")
  public ResponseEntity<?> indexProductType() {
    List<ProductTypeDTO> productTypes = productTypeService.getAllProductTypes();

    return ApiResponseDTO.ok(productTypes);
  }
}

package com.foodorder.foodapp.controller.api.user;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.evaluate.ClientEvaluateSearchDTO;
import com.foodorder.foodapp.dto.evaluate.ClientListEvaluateDTO;
import com.foodorder.foodapp.dto.product.ClientDetailProductDTO;
import com.foodorder.foodapp.dto.product.ClientProductDTO;
import com.foodorder.foodapp.dto.product.ClientSearchProductDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.response.PageResponseDTO;
import com.foodorder.foodapp.enums.EProductSort;
import com.foodorder.foodapp.service.ClientEvaluateService;
import com.foodorder.foodapp.service.ClientProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Validated
@Tag(name = "products", description = "Api Client for Products")
public class ProductsApiController {

  private final ClientProductService clientProductService;
  private final ClientEvaluateService clientEvaluateService;

  @GetMapping
  @Operation(summary = "List products", description = "Get list of products for client. All filters are optional and combined with AND")
  public ResponseEntity<?> indexProduct(
      @Parameter(description = "Part of the product name, case-insensitive") @RequestParam(required = false) String name,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long productTypeId,
      @Parameter(description = "Lowest price, inclusive") @RequestParam(required = false) @Min(value = 0, message = "{validation.product.min_price.min}") Integer minPrice,
      @Parameter(description = "Highest price, inclusive; must not be lower than minPrice") @RequestParam(required = false) @Min(value = 0, message = "{validation.product.max_price.min}") Integer maxPrice,
      @Parameter(description = "Average rating from this many stars up (1-5)") @RequestParam(required = false) @Min(value = 1, message = "{validation.product.min_rating.range}") @Max(value = 5, message = "{validation.product.min_rating.range}") Integer minRating,
      @Parameter(description = "Sort order, default NEWEST") @RequestParam(required = false) EProductSort sortBy,
      @ParameterObject Pageable pageable) {
    ClientSearchProductDTO params = new ClientSearchProductDTO(name, categoryId, productTypeId,
        minPrice, maxPrice, minRating);

    PageResponseDTO<ClientProductDTO> response = new PageResponseDTO<>(
        clientProductService.getPublicProducts(params, sortBy, pageable));

    return ApiResponseDTO.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get product details", description = "Get details of a product by ID")
  public ResponseEntity<?> detailProduct(@PathVariable Long id) {
    ClientDetailProductDTO product = clientProductService.getProductById(id);
    return ApiResponseDTO.ok(product);
  }

  @GetMapping("/{id}/evaluates")
  @Operation(summary = "List product evaluates", description = "Get visible evaluates of a product, newest first")
  public ResponseEntity<?> indexProductEvaluates(@PathVariable Long id,
      @ParameterObject Pageable pageable) {
    ClientEvaluateSearchDTO params = new ClientEvaluateSearchDTO(id);
    PageResponseDTO<ClientListEvaluateDTO> response = new PageResponseDTO<>(
        clientEvaluateService.getEvaluates(params, pageable));

    return ApiResponseDTO.ok(response);
  }
}

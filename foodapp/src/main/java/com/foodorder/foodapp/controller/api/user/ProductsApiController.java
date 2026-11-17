package com.foodorder.foodapp.controller.api.user;

import com.foodorder.foodapp.dto.pagination.PaginationDTO;
import com.foodorder.foodapp.config.properties.PaginationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.product.ClientDetailProductDTO;
import com.foodorder.foodapp.dto.product.ClientProductDTO;
import com.foodorder.foodapp.dto.product.SearchProductDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.response.PageResponseDTO;
import com.foodorder.foodapp.service.ClientProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Tag(name = "products", description = "Api Client for Products")
public class ProductsApiController {

  private final ClientProductService clientProductService;
  private final PaginationProperties paginationProperties;

  @GetMapping
  @Operation(summary = "List products", description = "Get list of products for client")
  public ResponseEntity<?> indexProduct(@RequestParam(required = false) String name,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long productTypeId,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer perPage) {
    PaginationDTO pagination = paginationProperties.resolve(page, perPage);
    SearchProductDTO params = new SearchProductDTO(name, categoryId, productTypeId, pagination.getPage(),
        pagination.getPerPage());

    PageResponseDTO<ClientProductDTO> response = new PageResponseDTO<>(clientProductService.getPublicProducts(params));

    return ApiResponseDTO.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get product details", description = "Get details of a product by ID")
  public ResponseEntity<?> detailProduct(@PathVariable Long id) {
    ClientDetailProductDTO product = clientProductService.getProductById(id);
    return ApiResponseDTO.ok(product);
  }
}

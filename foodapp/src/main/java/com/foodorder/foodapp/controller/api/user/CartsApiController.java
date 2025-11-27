package com.foodorder.foodapp.controller.api.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.cart.AddToCartDTO;
import com.foodorder.foodapp.dto.cart.DetailCartDTO;
import com.foodorder.foodapp.dto.cart.UpdateToCartDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.service.ClientCartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@AllArgsConstructor
@Validated
@Tag(name = "carts", description = "Api Client for Carts")
public class CartsApiController {
  private final ClientCartService clientCartService;

  @GetMapping("/me")
  @Operation(summary = "Cart info", description = "Get cart information for client")
  public ResponseEntity<?> getCart(@AuthenticationPrincipal User currentUser) {

    DetailCartDTO response = clientCartService.getCartByUserId(currentUser);

    return ApiResponseDTO.ok(response);
  }

  @PostMapping("/me/add")
  @Operation(summary = "Add item to cart", description = "Add an item to the cart for client")
  public ResponseEntity<?> addToCart(
      @Valid @RequestBody AddToCartDTO addToCartDTO,
      @AuthenticationPrincipal User currentUser) {

    DetailCartDTO response = clientCartService.addItemToCart(currentUser, addToCartDTO);

    return ApiResponseDTO.created(response, "/api/carts/" + response.getId());
  }

  @PostMapping("/me/update")
  @Operation(summary = "Update/remove item to cart", description = "Update or remove an item from the cart for client")

  public ResponseEntity<?> updateCart(
      @Valid @RequestBody UpdateToCartDTO updateToCartDTO,
      @AuthenticationPrincipal User currentUser) {

    DetailCartDTO response = clientCartService.updateCart(currentUser, updateToCartDTO);

    return ApiResponseDTO.created(response, "/api/carts/" + response.getId());
  }
}

package com.foodorder.foodapp.controller.api.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.foodorder.foodapp.dto.order.DetailOrderDTO;
import com.foodorder.foodapp.dto.order.ReasonCancelDTO;
import com.foodorder.foodapp.dto.order.SearchOrderDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.service.ClientOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@Validated
@Tag(name = "orders", description = "Api Client for Orders")
@SecurityRequirement(name = "bearerAuth")
public class OrdersApiController {
  private final ClientOrderService clientOrderService;

  @GetMapping
  @Operation(summary = "Order list", description = "Get order list for client")
  public ResponseEntity<?> getOrders(@RequestParam(required = false) String productName,
      @AuthenticationPrincipal User currentUser) {
    SearchOrderDTO params = new SearchOrderDTO(productName);

    List<DetailOrderDTO> response = clientOrderService.getAllOrders(currentUser, params);

    return ApiResponseDTO.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get order detail", description = "Get detail of an order for client")
  public ResponseEntity<?> getOrderDetail(
      @PathVariable Long id,
      @AuthenticationPrincipal User currentUser) {

    DetailOrderDTO response = clientOrderService.getOrderById(currentUser, id);

    return ApiResponseDTO.ok(response);
  }

  @PostMapping("/complete")
  @Operation(summary = "Complete order", description = "Complete and submit an order from the user's cart")
  public ResponseEntity<?> createOrder(@AuthenticationPrincipal User currentUser) {

    DetailOrderDTO response = clientOrderService.createOrder(currentUser);

    return ApiResponseDTO.created(response, "/api/orders/" + response.getId());
  }

  @PostMapping("/{id}/cancel")
  @Operation(summary = "Cancel order", description = "Cancel an order for client")
  public ResponseEntity<?> cancelOrder(
      @PathVariable Long id,
      @Valid @RequestBody ReasonCancelDTO reasonCancelDTO,
      @AuthenticationPrincipal User currentUser) {
    DetailOrderDTO response = clientOrderService.cancelOrder(id, currentUser, reasonCancelDTO);

    return ApiResponseDTO.ok(response);
  }
}

package com.foodorder.foodapp.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartDTO {
  @NotNull(message = "{validation.cart.product_id.required}")
  private Long productId;

  @NotNull(message = "{validation.cart.quantity.required}")
  @Min(value = 1, message = "{validation.cart.quantity.min}")
  private Integer quantity;
}

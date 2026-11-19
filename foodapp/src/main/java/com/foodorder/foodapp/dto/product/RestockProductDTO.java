package com.foodorder.foodapp.dto.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Admin "restock" action: adds this many units to the current stock. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockProductDTO {
  @NotNull(message = "{validation.product.restock.amount.range}")
  @Min(value = 1, message = "{validation.product.restock.amount.range}")
  @Max(value = 100_000, message = "{validation.product.restock.amount.range}")
  private Integer amount;
}

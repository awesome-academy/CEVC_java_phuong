package com.foodorder.foodapp.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CreateProductDTO extends ProductFormDTO {
  /** Initial stock, set only when the product is created. */
  @NotNull(message = "{validation.product.quantity.required}")
  @Min(value = 0, message = "{validation.product.quantity.min}")
  private Integer quantity;
}

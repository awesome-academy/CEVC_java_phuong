package com.foodorder.foodapp.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class UpdateProductDTO extends CreateProductDTO {
  @NotNull(message = "{validation.product.id.required}")
  private Long id;

  private String image;
}

package com.foodorder.foodapp.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Admin edit form. Has no quantity: the form was filled when the page opened, so writing its
 * stock back would undo every order placed meanwhile. Stock is changed with the restock action.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class UpdateProductDTO extends ProductFormDTO {
  @NotNull(message = "{validation.product.id.required}")
  private Long id;

  private String image;
}

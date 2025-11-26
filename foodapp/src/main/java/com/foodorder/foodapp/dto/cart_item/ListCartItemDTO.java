package com.foodorder.foodapp.dto.cart_item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListCartItemDTO {
  private Long id;

  private CartProductDTO product;

  private Integer quantity;
}

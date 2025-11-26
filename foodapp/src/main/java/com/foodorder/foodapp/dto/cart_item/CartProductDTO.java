package com.foodorder.foodapp.dto.cart_item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDTO {
  private Long id;

  private String name;

  private Integer price;
}

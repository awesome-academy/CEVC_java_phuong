package com.foodorder.foodapp.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class CartUserDTO {
  private Long id;

  private String fullName;
}

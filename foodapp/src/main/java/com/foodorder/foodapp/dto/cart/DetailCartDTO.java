package com.foodorder.foodapp.dto.cart;

import java.util.List;

import com.foodorder.foodapp.dto.cart_item.ListCartItemDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserDTO {
  private Long id;

  private String fullName;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DetailCartDTO {
  private Long id;

  private UserDTO user;

  private List<ListCartItemDTO> cartItems;

  private Integer totalPrice;

  public Integer getTotalPrice() {
    if (cartItems == null)
      return 0;
    return cartItems.stream()
        .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
        .sum();
  }
}

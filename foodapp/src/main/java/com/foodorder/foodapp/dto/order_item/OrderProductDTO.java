package com.foodorder.foodapp.dto.order_item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDTO {
  private Long id;

  private String name;
}

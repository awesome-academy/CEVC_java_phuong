package com.foodorder.foodapp.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOrderDTO {
  private Long productId;

  private Integer quantity;
}

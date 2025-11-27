package com.foodorder.foodapp.dto.order_item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListOrderItemDTO {
  private Long id;

  private OrderProductDTO product;

  private Integer quantity;

  private Integer price;
}

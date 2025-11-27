package com.foodorder.foodapp.dto.order;

import java.util.List;

import com.foodorder.foodapp.dto.order_item.ListOrderItemDTO;
import com.foodorder.foodapp.dto.order_status.OrderStatusDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DetailOrderDTO {
  private Long id;

  private OrderStatusDTO orderStatus;

  private String reasonCancel;

  private Integer totalPrice;

  private List<ListOrderItemDTO> orderItems;
}

package com.foodorder.foodapp.dto.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodorder.foodapp.dto.order_item.ListOrderItemDTO;
import com.foodorder.foodapp.dto.order_status.OrderStatusDTO;
import com.foodorder.foodapp.dto.user_address.DetailUserAddressDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOrderDTO {
  private Long id;

  private OrderStatusDTO orderStatus;

  private OrderUserDTO user;

  @JsonIgnore
  private Integer totalPrice;

  private List<ListOrderItemDTO> orderItems;

  private LocalDateTime createdAt;

  public String getTotalPrice() {
    if (totalPrice == null)
      return "0";
    return java.text.NumberFormat.getInstance(Locale.US).format(totalPrice);
  }

  private DetailUserAddressDTO orderAddress;

  private String reasonCancel;
}

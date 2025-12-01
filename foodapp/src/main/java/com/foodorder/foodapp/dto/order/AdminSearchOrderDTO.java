package com.foodorder.foodapp.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchOrderDTO {
  private String productName;

  private Long orderStatusId;

  private Long priceLevel;

  private int page = 1;

  private int perPage = 5;
}

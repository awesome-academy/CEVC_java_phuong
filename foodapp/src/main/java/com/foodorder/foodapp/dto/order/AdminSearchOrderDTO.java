package com.foodorder.foodapp.dto.order;

import com.foodorder.foodapp.dto.pagination.PaginationDTO;

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

  private PaginationDTO pagination;
}

package com.foodorder.foodapp.dto.order;

import com.foodorder.foodapp.config.PaginationConfig;
import com.foodorder.foodapp.dto.pagination.PaginationDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchOrderDTO {
  private String productName;

  private PaginationDTO pagination;

  public SearchOrderDTO(String productName, int page, int perPage, PaginationConfig paginationConfig) {
    this.productName = productName;
    this.pagination = new PaginationDTO(page, perPage, paginationConfig);
  }
}

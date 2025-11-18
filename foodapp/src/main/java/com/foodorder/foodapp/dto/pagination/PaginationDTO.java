package com.foodorder.foodapp.dto.pagination;

import com.foodorder.foodapp.config.PaginationConfig;

import lombok.Value;

@Value
public class PaginationDTO {
  private int page;

  private int perPage;

  public PaginationDTO(int page, int perPage, PaginationConfig paginationConfig) {
    this.page = page < 1 ? paginationConfig.getDefaultPage() : page;
    this.perPage = perPage < 1 ? paginationConfig.getDefaultPerPage() : perPage;
  }
}

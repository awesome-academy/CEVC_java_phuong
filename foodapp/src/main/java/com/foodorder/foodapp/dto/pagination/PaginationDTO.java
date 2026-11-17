package com.foodorder.foodapp.dto.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.foodorder.foodapp.config.PaginationConfig;

import lombok.Value;

@Value
public class PaginationDTO {
  // Upper bound for perPage sent by clients (e.g. perPage=100000 would load everything in one response)
  public static final int MAX_PER_PAGE = 50;

  private int page;

  private int perPage;

  public PaginationDTO(int page, int perPage, PaginationConfig paginationConfig) {
    this.page = page < 1 ? paginationConfig.getDefaultPage() : page;
    this.perPage = perPage < 1 ? paginationConfig.getDefaultPerPage() : Math.min(perPage, MAX_PER_PAGE);
  }

  /** Page numbers are 1-based in the API, 0-based in Spring Data. */
  public Pageable toPageable(Sort sort) {
    return PageRequest.of(page - 1, perPage, sort);
  }
}

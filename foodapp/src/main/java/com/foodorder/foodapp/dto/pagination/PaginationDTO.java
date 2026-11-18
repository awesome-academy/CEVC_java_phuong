package com.foodorder.foodapp.dto.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Value;

/**
 * Normalized page/perPage of a list request. Built once in the controller (see PaginationProperties#resolve),
 * so search DTOs, templates and services always get valid values.
 */
@Value
public class PaginationDTO {
  // Upper bound for perPage sent by clients (e.g. perPage=100000 would load everything in one response)
  public static final int MAX_PER_PAGE = 50;

  private int page;

  private int perPage;

  private PaginationDTO(int page, int perPage) {
    this.page = page;
    this.perPage = perPage;
  }

  /** Missing or < 1 values fall back to the defaults; perPage is capped at MAX_PER_PAGE. */
  public static PaginationDTO of(Integer page, Integer perPage, int defaultPage, int defaultPerPage) {
    int resolvedPage = page == null || page < 1 ? defaultPage : page;
    int resolvedPerPage = perPage == null || perPage < 1 ? defaultPerPage : Math.min(perPage, MAX_PER_PAGE);
    return new PaginationDTO(resolvedPage, resolvedPerPage);
  }

  /** Page numbers are 1-based in the API/UI, 0-based in Spring Data. */
  public Pageable toPageable(Sort sort) {
    return PageRequest.of(page - 1, perPage, sort);
  }
}

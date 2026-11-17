package com.foodorder.foodapp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.foodorder.foodapp.dto.pagination.PaginationDTO;

import jakarta.validation.constraints.Min;

/**
 * app.pagination.*: defaults used when a request sends no or invalid page/perPage.
 * Validated at startup, so e.g. default-per-page: 0 stops the app instead of failing on the first list page.
 * This record is the only place reading these keys.
 */
@Validated
@ConfigurationProperties(prefix = "app.pagination")
public record PaginationProperties(@Min(1) int defaultPage, @Min(1) int defaultPerPage) {

  /** Normalize raw request values (null when the parameter is missing). */
  public PaginationDTO resolve(Integer page, Integer perPage) {
    return PaginationDTO.of(page, perPage, defaultPage, defaultPerPage);
  }
}

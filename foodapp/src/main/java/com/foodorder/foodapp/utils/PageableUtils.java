package com.foodorder.foodapp.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Page/size come from the request (normalized by spring.data.web.pageable.*: missing or < 1 -> default,
 * capped at max-page-size); the sort is always chosen by the server.
 */
public final class PageableUtils {

  private PageableUtils() {
  }

  /** Drops any ?sort= sent by the client, so an unknown property cannot turn into a 500. */
  public static Pageable withFixedSort(Pageable pageable, Sort sort) {
    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
  }
}

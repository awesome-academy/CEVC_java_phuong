package com.foodorder.foodapp.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * View helper for the shared admin pagination fragment (admin/layouts/pagination.html),
 * used from Thymeleaf as {@code @paginationLinks}.
 */
@Component("paginationLinks")
public class PaginationLinks {

  /** Pages shown on each side of the current page. */
  private static final int SIDE = 2;

  /**
   * Current request URL with only {@code page} replaced, so every filter and perPage in the
   * query string is kept without each template listing them.
   */
  public String url(int page) {
    // Path + query only (no scheme/host), so links stay correct behind a proxy
    UriComponents current = ServletUriComponentsBuilder.fromCurrentRequest().build(true);
    return UriComponentsBuilder.newInstance()
        .path(current.getPath())
        .query(current.getQuery())
        .replaceQueryParam("page", page)
        .build(true)
        .toUriString();
  }

  /**
   * One-based page numbers to render: first, last and current ± {@link #SIDE}; {@code null}
   * marks a gap ("…"). E.g. current 7 of 20 → 1 … 5 6 7 8 9 … 20.
   */
  public List<Integer> window(int current, int total) {
    List<Integer> pages = new ArrayList<>();
    // A gap hiding a single page shows that page instead of "…"
    int from = Math.max(2, current - SIDE);
    int to = Math.min(total - 1, current + SIDE);
    if (from == 3) {
      from = 2;
    }
    if (to == total - 2) {
      to = total - 1;
    }

    pages.add(1);
    if (from > 2) {
      pages.add(null);
    }
    for (int i = from; i <= to; i++) {
      pages.add(i);
    }
    if (to < total - 1) {
      pages.add(null);
    }
    if (total > 1) {
      pages.add(total);
    }
    return pages;
  }
}

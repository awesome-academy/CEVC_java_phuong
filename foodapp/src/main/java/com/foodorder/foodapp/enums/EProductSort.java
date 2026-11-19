package com.foodorder.foodapp.enums;

import org.springframework.data.domain.Sort;

/**
 * Sort options of the public product list. The client picks one by name, so only these
 * whitelisted columns can reach ORDER BY (an unknown value is rejected with 400).
 */
public enum EProductSort {
  NEWEST(Sort.by("id").descending()),
  NAME_ASC(Sort.by("name").ascending()),
  NAME_DESC(Sort.by("name").descending()),
  PRICE_ASC(Sort.by("price").ascending()),
  PRICE_DESC(Sort.by("price").descending()),
  RATING_DESC(Sort.by("averageRating").descending());

  private final Sort sort;

  EProductSort(Sort sort) {
    this.sort = sort;
  }

  /** The sort, with id as tie-breaker so products with equal values keep a stable order across pages. */
  public Sort toSort() {
    return this == NEWEST ? sort : sort.and(Sort.by("id").descending());
  }
}

package com.foodorder.foodapp.enums;

import org.springframework.data.domain.Sort;

import com.foodorder.foodapp.model.BaseModel_;
import com.foodorder.foodapp.model.Product_;

/**
 * Sort options of the public product list. The client picks one by name, so only these
 * whitelisted columns can reach ORDER BY (an unknown value is rejected with 400).
 * Column names come from the metamodel String constants (compile-time constants, unlike the
 * attribute fields Hibernate fills at startup), so renaming a Product field breaks the build.
 */
public enum EProductSort {
  NEWEST(Sort.by(BaseModel_.ID).descending()),
  NAME_ASC(Sort.by(Product_.NAME).ascending()),
  NAME_DESC(Sort.by(Product_.NAME).descending()),
  PRICE_ASC(Sort.by(Product_.PRICE).ascending()),
  PRICE_DESC(Sort.by(Product_.PRICE).descending()),
  RATING_DESC(Sort.by(Product_.AVERAGE_RATING).descending());

  private final Sort sort;

  EProductSort(Sort sort) {
    this.sort = sort;
  }

  /** The sort, with id as tie-breaker so products with equal values keep a stable order across pages. */
  public Sort toSort() {
    return this == NEWEST ? sort : sort.and(Sort.by(BaseModel_.ID).descending());
  }
}

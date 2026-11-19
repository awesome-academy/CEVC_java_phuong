package com.foodorder.foodapp.enums;

/**
 * Sort options of the public product list. The client picks one by name, so only these
 * whitelisted options can reach ORDER BY (an unknown value is rejected with 400).
 * The column each option sorts by is decided in ClientProductService.toSort, so this package
 * stays free of model imports (entities import enums, not the other way round).
 */
public enum EProductSort {
  NEWEST,
  NAME_ASC,
  NAME_DESC,
  PRICE_ASC,
  PRICE_DESC,
  RATING_DESC
}

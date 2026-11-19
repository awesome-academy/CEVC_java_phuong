package com.foodorder.foodapp.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Filters of the public product list (all optional, combined with AND). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSearchProductDTO {
  private String name;

  private Long categoryId;

  private Long productTypeId;

  private Integer minPrice;

  private Integer maxPrice;

  /** Average rating from this many stars up. */
  private Integer minRating;
}

package com.foodorder.foodapp.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Product as an option of a filter dropdown. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDTO {
  private Long id;

  private String name;
}

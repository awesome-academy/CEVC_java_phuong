package com.foodorder.foodapp.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductDTO {
  private String name;

  private Long categoryId;

  private Long productTypeId;

  private int page;

  private int perPage;
}

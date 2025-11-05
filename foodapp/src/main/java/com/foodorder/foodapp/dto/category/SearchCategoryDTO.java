package com.foodorder.foodapp.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCategoryDTO {
  private String name;

  private int page;

  private int perPage;
}

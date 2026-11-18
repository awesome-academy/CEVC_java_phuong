package com.foodorder.foodapp.dto.category;

import com.foodorder.foodapp.dto.pagination.PaginationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCategoryDTO {
  private String name;

  private PaginationDTO pagination;
}

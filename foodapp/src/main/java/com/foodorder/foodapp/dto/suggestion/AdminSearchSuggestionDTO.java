package com.foodorder.foodapp.dto.suggestion;

import com.foodorder.foodapp.dto.pagination.PaginationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchSuggestionDTO {
  private String status;

  private PaginationDTO pagination;
}

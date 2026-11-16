package com.foodorder.foodapp.dto.suggestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchSuggestionDTO {
  private String status;

  private int page = 1;

  private int perPage = 5;
}

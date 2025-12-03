package com.foodorder.foodapp.dto.evaluate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchEvaluateDTO {
  private String productName;

  private Long ratingLevel;

  private int page = 1;

  private int perPage = 5;
}

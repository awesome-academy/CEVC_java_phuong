package com.foodorder.foodapp.dto.evaluate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchEvaluateDTO {
  private Long productId;

  private Long ratingLevel;

  /** null = all, true = shown only, false = hidden only. */
  private Boolean isShow;
}

package com.foodorder.foodapp.dto.evaluate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientMyEvaluateDTO {
  private Long id;

  private Integer rating;

  private String content;

  private String image;

  private Boolean isShow;

  private EvaluateProductDTO product;

  private LocalDateTime createdAt;
}

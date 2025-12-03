package com.foodorder.foodapp.dto.evaluate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailEvaluateDTO {
  private Long id;

  private String content;

  private Integer rating;

  private Boolean isShow;

  private String image;

  private EvaluateProductDTO product;

  private EvaluateUserDTO user;

  private LocalDateTime createdAt;
}

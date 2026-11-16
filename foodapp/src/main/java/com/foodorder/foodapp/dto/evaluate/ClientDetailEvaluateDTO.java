package com.foodorder.foodapp.dto.evaluate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString 
public class ClientDetailEvaluateDTO {
  private Long id;

  private String content;

  private Integer rating;

  private String image;
}

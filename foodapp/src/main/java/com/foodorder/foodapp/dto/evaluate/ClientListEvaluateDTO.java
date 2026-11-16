package com.foodorder.foodapp.dto.evaluate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class ClientListEvaluateDTO {
  private Long id;

  private String content;

  private Integer rating;

  private String image;

  private EvaluateUserDTO user;

  private LocalDateTime createdAt;
}

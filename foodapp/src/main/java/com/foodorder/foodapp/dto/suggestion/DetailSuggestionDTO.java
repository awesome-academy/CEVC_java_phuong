package com.foodorder.foodapp.dto.suggestion;

import java.time.LocalDateTime;

import com.foodorder.foodapp.dto.user.UserOptionDTO;
import com.foodorder.foodapp.enums.ESuggestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailSuggestionDTO {
  private Long id;

  private String title;

  private String description;

  private String image;

  private ESuggestion status;

  private UserOptionDTO user;

  private LocalDateTime createdAt;
}

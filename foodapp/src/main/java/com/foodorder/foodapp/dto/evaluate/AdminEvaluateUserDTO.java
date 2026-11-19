package com.foodorder.foodapp.dto.evaluate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Evaluate author as seen by admins; kept apart from EvaluateUserDTO so the public API never exposes email. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEvaluateUserDTO {
  private Long id;

  private String fullName;

  private String email;

  private String avatar;
}

package com.foodorder.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
  private Long id;

  private String email;

  private String accessToken;

  private String refreshToken;
}

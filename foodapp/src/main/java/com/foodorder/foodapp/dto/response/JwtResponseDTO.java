package com.foodorder.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
  private Long id;

  private String email;

  @ToString.Exclude // never in logs (Spring DEBUG logs request/response bodies)

  private String accessToken;

  @ToString.Exclude // never in logs (Spring DEBUG logs request/response bodies)

  private String refreshToken;
}

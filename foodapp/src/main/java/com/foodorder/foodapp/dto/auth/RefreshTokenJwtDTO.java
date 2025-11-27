package com.foodorder.foodapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RefreshTokenJwtDTO {
  @NotBlank(message = "{validation.user.refresh_token.required}")
  @Size(max = 1000, message = "{validation.user.refresh_token.max}")
  private String refreshToken;
}

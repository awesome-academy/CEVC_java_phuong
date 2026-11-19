package com.foodorder.foodapp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Body of POST /api/auth/social/{provider}. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginDTO {
  @Schema(description = "Token issued by the provider. Google: the ID token (credential) returned by Google Identity Services",
      example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2...")
  @NotBlank(message = "{validation.auth.social.token.required}")
  @ToString.Exclude // never in logs (Spring DEBUG logs request/response bodies)
  private String token;
}

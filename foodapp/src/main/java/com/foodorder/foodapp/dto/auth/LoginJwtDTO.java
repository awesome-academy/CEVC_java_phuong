package com.foodorder.foodapp.dto.auth;

import com.foodorder.foodapp.constants.RegexConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoginJwtDTO {
  @NotBlank(message = "{validation.user.email.required}")
  @Size(max = 255, message = "{validation.user.email.max}")
  @Email(message = "{validation.user.email.format}")
  private String email;

  @NotBlank(message = "{validation.user.password.required}")
  @Pattern(regexp = RegexConstants.PW_REGEX, message = "{validation.user.password.invalid}")
  @ToString.Exclude // never in logs (Spring DEBUG logs request/response bodies)
  private String password;
}

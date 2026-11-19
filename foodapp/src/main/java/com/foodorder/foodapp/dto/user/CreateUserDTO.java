package com.foodorder.foodapp.dto.user;

import com.foodorder.foodapp.constants.RegexConstants;
import com.foodorder.foodapp.validation.UniqueEmail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@UniqueEmail
public class CreateUserDTO extends UserOperationDTO {
  private Long id;

  @NotBlank(message = "{validation.user.password.required}")
  @Pattern(regexp = RegexConstants.PW_REGEX, message = "{validation.user.password.invalid}")
  @ToString.Exclude // never in logs (Spring DEBUG logs request/response bodies)
  private String password;

  @NotNull(message = "{validation.user.role.required}")
  private Long roleId;

  @NotNull(message = "{validation.user.auth_provider.required}")
  private Long authProviderId;
}

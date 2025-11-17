package com.foodorder.foodapp.dto.user;

import com.foodorder.foodapp.constants.RegexConstants;
import com.foodorder.foodapp.validation.UniqueEmail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@UniqueEmail
public class UpdateUserDTO extends UserOperationDTO {
  @NotNull(message = "{validation.user.id.required}")
  private Long id;

  @Pattern(regexp = RegexConstants.PW_OPTIONAL_REGEX, message = "{validation.user.password.invalid}")
  private String password;

  @NotNull(message = "{validation.user.role.required}")
  private Long roleId;

  @NotNull(message = "{validation.user.auth_provider.required}")
  private Long authProviderId;

  private String avatar;
}

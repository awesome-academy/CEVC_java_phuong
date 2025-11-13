package com.foodorder.foodapp.dto.user;

import com.foodorder.foodapp.constants.RegexConstants;
import com.foodorder.foodapp.validation.UniqueEmail;

import jakarta.validation.constraints.NotBlank;
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
public class CreateUserDTO extends UserOperationDTO {
  private Long id;

  @NotBlank(message = "{validation.user.password.required}")
  @Pattern(regexp = RegexConstants.PW_REGEX, message = "{validation.user.password.invalid}")
  private String password;
}

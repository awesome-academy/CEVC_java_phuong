package com.foodorder.foodapp.dto.user;

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

  @Pattern(regexp = "^$|[a-zA-Z0-9]{6,10}", message = "{validation.user.password.invalid}")
  private String password;

  private String avatar;
}
